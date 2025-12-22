package com.github.salandora.sophisticatedlibrary.loot;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.LootTableIdCondition;
import com.github.salandora.sophisticatedlibrary.loot.api.v1.extensions.SophisticatedLootTableBuilder;
import com.github.salandora.sophisticatedlibrary.loot.impl.LootModifierManager;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class SophisticatedLoot implements ModInitializer {
	public static final ResourceKey<Registry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS_KEY = SophisticatedLibrary.key("global_loot_modifier_serializers");

	public static final Registry<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = FabricRegistryBuilder.createSimple(GLOBAL_LOOT_MODIFIER_SERIALIZERS_KEY).buildAndRegister();

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, SophisticatedLibrary.id("loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LootModifierManager.INSTANCE);

		ResourceLocation last = SophisticatedLibrary.id("last");
		LootTableEvents.MODIFY.addPhaseOrdering(Event.DEFAULT_PHASE, last);
		LootTableEvents.MODIFY.register(last, (key, manager, id, builder, source) ->
				((SophisticatedLootTableBuilder) builder).sophisticatedLibrary_setId(id));
	}

	public static ObjectArrayList<ItemStack> modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		context.sophisticatedLibrary_setQueriedLootTableId(lootTableId);
		LootModifierManager man = LootModifierManager.INSTANCE;
		for (IGlobalLootModifier mod : man.getAllLootMods()) {
			generatedLoot = mod.apply(generatedLoot, context);
		}
		return generatedLoot;
	}
}
