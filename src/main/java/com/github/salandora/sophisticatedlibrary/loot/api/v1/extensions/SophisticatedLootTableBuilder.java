package com.github.salandora.sophisticatedlibrary.loot.api.v1.extensions;

import net.minecraft.resources.ResourceLocation;

public interface SophisticatedLootTableBuilder {
	default void sophisticatedLibrary_setId(ResourceLocation id) {
		throw new RuntimeException("This was not implemented properly.");
	}
}
