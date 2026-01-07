package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component;

import net.minecraft.world.item.ItemStack;

public interface SophisticatedItemContainerContents {
	default int sophisticatedLibrary_getSlots() {
		throw new RuntimeException("Should have been overriden by mixin.");
	}
	default ItemStack sophisticatedLibrary_getStackInSlot(int slot) {
		throw new RuntimeException("Should have been overriden by mixin.");
	}
}
