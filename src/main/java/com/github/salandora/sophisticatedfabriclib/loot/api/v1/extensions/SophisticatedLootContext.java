package com.github.salandora.sophisticatedfabriclib.loot.api.v1.extensions;

import net.minecraft.resources.ResourceLocation;

public interface SophisticatedLootContext {
	default void sophisticatedLibrary_setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		throw new RuntimeException("This was not implemented properly.");
	}

	default ResourceLocation sophisticatedLibrary_getQueriedLootTableId() {
		throw new RuntimeException("This was not implemented properly.");
	}
}
