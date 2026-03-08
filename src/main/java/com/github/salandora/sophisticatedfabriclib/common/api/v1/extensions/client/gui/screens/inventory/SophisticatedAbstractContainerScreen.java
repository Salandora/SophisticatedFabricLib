package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.client.gui.screens.inventory;

import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;

public interface SophisticatedAbstractContainerScreen {
    default int sophisticatedFabricLibrary_getSlotColor(int slotId) {
        return -2130706433;
    }

    default int sophisticatedFabricLibrary_getXSize() {
        throw new RuntimeException("Should have been overridden by mixin.");
    }

    default int sophisticatedFabricLibrary_getGuiLeft() {
        throw new RuntimeException("Should have been overridden by mixin.");
    }

    default int sophisticatedFabricLibrary_getGuiTop() {
        throw new RuntimeException("Should have been overridden by mixin.");
    }

    @Nullable
    default Slot sophisticatedFabricLibrary_getSlotUnderMouse() {
        throw new RuntimeException("Should have been overridden by mixin.");
    }
}
