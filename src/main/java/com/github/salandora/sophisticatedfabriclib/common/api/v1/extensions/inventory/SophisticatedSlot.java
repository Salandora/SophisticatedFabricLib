package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SophisticatedSlot {
    default boolean sophisticatedFabricLibrary_isSameInventory(Slot other) {
        return ((Slot)this).container == other.container;
    }

    default int sophisticatedFabricLibrary_getSlotIndex() {
        return 0;
    }

    default Slot sophisticatedFabricLibrary_setBackground(ResourceLocation atlas, ResourceLocation sprite) {
        throw new RuntimeException("Should have been overriden by mixin.");
    }
}
