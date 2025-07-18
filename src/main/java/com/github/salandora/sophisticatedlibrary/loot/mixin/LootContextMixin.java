package com.github.salandora.sophisticatedlibrary.loot.mixin;

import com.github.salandora.sophisticatedlibrary.loot.LootTableIdCondition;
import com.github.salandora.sophisticatedlibrary.loot.extensions.SophisticatedLootContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootContext.class)
public class LootContextMixin implements SophisticatedLootContext {
	@Unique
	private ResourceLocation sophisticatedLibrary$queriedLootTableId;

	@Override
	public void sophisticatedLibrary$setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		this.sophisticatedLibrary$queriedLootTableId = queriedLootTableId;
	}

	@Override
	public ResourceLocation sophisticatedLibrary$getQueriedLootTableId() {
		return this.sophisticatedLibrary$queriedLootTableId == null ? LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.sophisticatedLibrary$queriedLootTableId;
	}
}
