package com.github.salandora.sophisticatedlibrary.loot.extensions;

import net.minecraft.resources.ResourceLocation;

public interface SophisticatedLootTableBuilder {
	default void sophisticatedLibrary$setId(ResourceLocation id) {
		throw new RuntimeException("This was not implemented properly.");
	}
}
