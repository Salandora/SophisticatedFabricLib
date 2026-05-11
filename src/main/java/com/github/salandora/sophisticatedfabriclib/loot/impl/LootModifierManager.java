package com.github.salandora.sophisticatedfabriclib.loot.impl;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LootModifierManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private static final String directory = "loot_modifiers";
	public static final ResourceLocation ID = SophisticatedFabricLib.id(directory);

	public static final LootModifierManager INSTANCE = new LootModifierManager();

	private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
	private HolderLookup.Provider registries;

	public LootModifierManager() {
		super(GSON, directory);
	}

	public void setRegistries(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, ProfilerFiller profiler) {
		profiler.push("LootModifierManager prepare: %s".formatted(getName()));

		Map<ResourceLocation, JsonElement> map = super.prepare(resourceManager, profiler);

		ResourceLocation global_loot_modifiers = ResourceLocation.fromNamespaceAndPath("sophisticated", directory + "/global_loot_modifiers.json");
		List<ResourceLocation> finalLocations = new ArrayList<>();
		for (Resource resource : resourceManager.getResourceStack(global_loot_modifiers)) {
			try (Reader reader = resource.openAsReader()) {
				profiler.push("LootModifierManager prepare resource: %s".formatted(resource.sourcePackId()));

				JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
				if (GsonHelper.getAsBoolean(jsonObject, "replace", false)) {
					finalLocations.clear();
				}

				JsonArray entries = GsonHelper.getAsJsonArray(jsonObject, "entries");
				for (int i = 0; i < entries.size(); i++) {
					ResourceLocation location = ResourceLocation.parse(GsonHelper.convertToString(entries.get(i), "entries[" + i + "]"));
					finalLocations.remove(location); // Update ordering
					finalLocations.add(location);
				}

				profiler.pop();
			} catch (RuntimeException | IOException exception) {
				SophisticatedFabricLib.LOGGER.error("Couldn't read global loot modifier list '{}' in data pack '{}'", global_loot_modifiers, resource.sourcePackId(), exception);
			}
		}

		Map<ResourceLocation, JsonElement> collect = finalLocations.stream().collect(Collectors.toMap(Function.identity(), map::get));

		profiler.pop();
		return collect;
	}

	protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, ProfilerFiller profiler) {
		profiler.push("LootModifierManager apply: %s".formatted(getName()));

		DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
		ImmutableMap.Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
		for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
			ResourceLocation location = entry.getKey();
			profiler.push("LootModifierManager apply: %s".formatted(location));

			JsonElement json = entry.getValue();
			IGlobalLootModifier.DIRECT_CODEC.parse(ops, json)
					.resultOrPartial(errorMsg -> SophisticatedFabricLib.LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, errorMsg))
					.ifPresent(carrier -> builder.put(location, carrier));

			profiler.pop();
		}
		this.registeredLootModifiers = builder.build();

		profiler.pop();
	}

	public Collection<IGlobalLootModifier> getAllLootMods() {
		return registeredLootModifiers.values();
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}
}
