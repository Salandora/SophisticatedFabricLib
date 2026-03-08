package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component;

import net.minecraft.world.item.ItemStack;

public interface SophisticatedItemContainerContents {
	default int sophisticatedFabricLibrary_getSlots() {
		throw new RuntimeException("Should have been overridden by mixin.");
	}

	default ItemStack sophisticatedFabricLibrary_getStackInSlot(int slot) {
		throw new RuntimeException("Should have been overridden by mixin.");
	}
}
