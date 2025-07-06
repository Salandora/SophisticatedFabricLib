package com.github.salandora.sophisticatedlibrary.loot;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.nio.file.Path;
import java.util.*;
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

	protected abstract void generate(BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> consumer);

	@Override
	public final CompletableFuture<?> run(CachedOutput writer) {
		HashMap<ResourceLocation, IGlobalLootModifier> builders = Maps.newHashMap();
		HashMap<ResourceLocation, ResourceCondition[]> conditionMap = new HashMap<>();

		return this.registriesLookup.thenCompose(lookup -> {
			this.generate((registryKey, builder) -> {
				ResourceCondition[] conditions = FabricDataGenHelper.consumeConditions(builder);
				conditionMap.put(registryKey.location(), conditions);

				if (builders.put(registryKey.location(), builder) != null) {
					throw new IllegalStateException("Duplicate loot table " + registryKey.location());
				}
			});

			Path globalLootModifierPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("sophisticated").resolve("loot_modifiers").resolve("global_loot_modifiers.json");
			Path modifierFolderPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("loot_modifiers");
			List<ResourceLocation> entries = new ArrayList<>();

			RegistryOps<JsonElement> ops = lookup.createSerializationContext(JsonOps.INSTANCE);
			ImmutableList.Builder<CompletableFuture<?>> futures = new ImmutableList.Builder<>();

			for (Map.Entry<ResourceLocation, IGlobalLootModifier> entry : builders.entrySet()) {
				var name = entry.getKey();
				entries.add(ResourceLocation.fromNamespaceAndPath(modid, name.getPath()));

				JsonObject tableJson = (JsonObject) IGlobalLootModifier.DIRECT_CODEC.encodeStart(ops, entry.getValue()).getOrThrow(IllegalStateException::new);
				FabricDataGenHelper.addConditions(tableJson, conditionMap.remove(entry.getKey()));

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

	public BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> withConditions(BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> exporter, ResourceCondition... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return (id, table) -> {
			FabricDataGenHelper.addConditions(table, conditions);
			exporter.accept(id, table);
		};
	}

	@Override
	public String getName() {
		return "Global Loot Modifiers: " + modid;
	}
}