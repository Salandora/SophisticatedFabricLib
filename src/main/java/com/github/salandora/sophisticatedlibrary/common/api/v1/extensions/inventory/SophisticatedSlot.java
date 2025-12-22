package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SophisticatedSlot {
    default boolean sophisticatedLibrary_isSameInventory(Slot other) {
        return ((Slot)this).container == other.container;
    }

    default int sophisticatedLibrary_getSlotIndex() {
        return 0;
    }

    default Slot sophisticatedLibrary_setBackground(ResourceLocation atlas, ResourceLocation sprite) {
        throw new RuntimeException("Should have been overriden by mixin.");
    }

	default ItemVariant sophisticatedLibrary_getItemVariant() {
		return ItemVariant.of(((Slot)this).getItem());
	}
}
