package com.github.salandora.sophisticatedlibrary.datagen;

import com.github.salandora.sophisticatedlibrary.loot.LootMultiplierModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.GlobalLootModifierProvider;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootTableIdCondition;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.BiConsumer;

public class DataGeneratorTestEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider((output, registriesFuture) -> new GlobalLootModifierProvider(output, registriesFuture, "global_loot_modifier_test") {
			@Override
			protected void generate(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer) {
				consumer.accept(BuiltInLootTables.SIMPLE_DUNGEON,
						new LootMultiplierModifier(
								new LootItemCondition[] { new LootTableIdCondition.Builder(BuiltInLootTables.SIMPLE_DUNGEON).build()	},
								2)
				);
			}
		});
	}
}
