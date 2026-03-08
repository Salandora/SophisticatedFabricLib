package com.github.salandora.sophisticatedfabriclib.loot.mixin;

import com.github.salandora.sophisticatedfabriclib.loot.api.v1.LootTableIdCondition;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.extensions.SophisticatedLootContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootContext.class)
public class LootContextMixin implements SophisticatedLootContext {
	@Unique
	private ResourceLocation sophisticatedFabricLibrary$queriedLootTableId;

	@Override
	public void sophisticatedFabricLibrary_setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		this.sophisticatedFabricLibrary$queriedLootTableId = queriedLootTableId;
	}

	@Override
	public ResourceLocation sophisticatedFabricLibrary_getQueriedLootTableId() {
		return this.sophisticatedFabricLibrary$queriedLootTableId == null ? LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.sophisticatedFabricLibrary$queriedLootTableId;
	}
}
