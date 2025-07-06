package com.github.salandora.sophisticatedlibrary.loot.extensions;

import net.minecraft.resources.ResourceLocation;

public interface SophisticatedLootContextExtensions {
	default void sophisticatedLibrary$setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		throw new RuntimeException("This was not implemented properly.");
	}

	default ResourceLocation sophisticatedLibrary$getQueriedLootTableId() {
		throw new RuntimeException("This was not implemented properly.");
	}
}
