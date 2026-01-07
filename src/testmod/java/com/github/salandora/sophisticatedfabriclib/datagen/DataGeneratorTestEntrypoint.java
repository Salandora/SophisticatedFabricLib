package com.github.salandora.sophisticatedfabriclib.datagen;

import com.github.salandora.sophisticatedfabriclib.loot.LootMultiplierModifier;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.GlobalLootModifierProvider;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.LootTableIdCondition;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.BiConsumer;

public class DataGeneratorTestEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider((output, registriesFuture) -> new GlobalLootModifierProvider(output, registriesFuture, "global_loot_modifier_test") {
			@Override
			protected void generate(BiConsumer<ResourceKey<LootTable>, IGlobalLootModifier> consumer) {
				consumer.accept(BuiltInLootTables.SIMPLE_DUNGEON,
						new LootMultiplierModifier(
								new LootItemCondition[] { new LootTableIdCondition.Builder(BuiltInLootTables.SIMPLE_DUNGEON.location()).build()	},
								2)
				);
			}
		});
	}
}
