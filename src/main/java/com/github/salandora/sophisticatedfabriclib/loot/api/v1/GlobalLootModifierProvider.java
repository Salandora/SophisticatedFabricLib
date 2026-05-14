package com.github.salandora.sophisticatedfabriclib.loot.api.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class GlobalLootModifierProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final PackOutput output;
	private final CompletableFuture<HolderLookup.Provider> registriesLookup;
	private final String modid;
	private boolean replace = false;

	public GlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
		this.output = output;
		this.registriesLookup = registries;
		this.modid = modid;
	}

	public void replace(boolean replace) {
		this.replace = replace;
	}

	protected abstract void generate(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer);

	@Override
	public final CompletableFuture<?> run(CachedOutput writer) {
		HashMap<ResourceLocation, IGlobalLootModifier> builders = Maps.newHashMap();
		HashMap<ResourceLocation, ConditionJsonProvider[]> conditionMap = new HashMap<>();

		return this.registriesLookup.thenCompose(lookup -> {
			this.generate((registryKey, builder) -> {
				ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(builder);
				conditionMap.put(registryKey, conditions);

				if (builders.put(registryKey, builder) != null) {
					throw new IllegalStateException("Duplicate loot table " + registryKey);
				}
			});

			Path globalLootModifierPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("sophisticated").resolve("loot_modifiers").resolve("global_loot_modifiers.json");
			Path modifierFolderPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("loot_modifiers");
			List<ResourceLocation> entries = new ArrayList<>();

			ImmutableList.Builder<CompletableFuture<?>> futures = new ImmutableList.Builder<>();

			for (Map.Entry<ResourceLocation, IGlobalLootModifier> entry : builders.entrySet()) {
				var name = entry.getKey();
				entries.add(new ResourceLocation(modid, name.getPath()));

				JsonObject tableJson = (JsonObject) IGlobalLootModifier.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow(false, s -> {});
				ConditionJsonProvider.write(tableJson, conditionMap.remove(entry.getKey()));

				Path modifierPath = modifierFolderPath.resolve(name.getPath() + ".json");
				futures.add(DataProvider.saveStable(writer, tableJson, modifierPath));
			}

			JsonObject json = new JsonObject();
			json.addProperty("replace", this.replace);
			json.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

			futures.add(DataProvider.saveStable(writer, json, globalLootModifierPath));

			return CompletableFuture.allOf(futures.build().toArray(CompletableFuture[]::new));
		});
	}

	@Override
	public String getName() {
		return "Global Loot Modifiers: " + modid;
	}
}