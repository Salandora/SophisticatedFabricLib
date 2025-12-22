package com.github.salandora.sophisticatedlibrary.loot;

import com.github.salandora.sophisticatedlibrary.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedlibrary.util.DeferredHolder;
import com.github.salandora.sophisticatedlibrary.util.DeferredRegister;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

public class GlobalLootModifierHelper implements ModInitializer {
	private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "global_loot_modifier_test");

	private static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<LootMultiplierModifier>> LOOT_MULTIPLIER_MODIFIER = GLOBAL_LOOT_MODIFIER.register("loot_multiplier", LootMultiplierModifier.CODEC);

	@Override
	public void onInitialize() {
		GLOBAL_LOOT_MODIFIER.register();
	}
}
