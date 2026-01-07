package com.github.salandora.sophisticatedfabriclib.loot;

import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedfabriclib.util.DeferredHolder;
import com.github.salandora.sophisticatedfabriclib.util.DeferredRegister;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;

public class GlobalLootModifierHelper implements ModInitializer {
	private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "global_loot_modifier_test");

	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<LootMultiplierModifier>> LOOT_MULTIPLIER_MODIFIER = GLOBAL_LOOT_MODIFIER.register("loot_multiplier", LootMultiplierModifier.CODEC);

	@Override
	public void onInitialize() {
		GLOBAL_LOOT_MODIFIER.register();
	}
}
