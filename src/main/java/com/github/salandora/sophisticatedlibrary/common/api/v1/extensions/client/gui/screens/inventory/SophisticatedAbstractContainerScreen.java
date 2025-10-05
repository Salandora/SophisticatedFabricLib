package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.client.gui.screens.inventory;

import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;

public interface SophisticatedAbstractContainerScreen {
    default int sophisticatedLibrary_getSlotColor(int slotId) {
        return -2130706433;
    }

    default int sophisticatedLibrary_getXSize() {
        throw new RuntimeException("Should have been overriden by mixin.");
    }

    default int sophisticatedLibrary_getGuiLeft() {
        throw new RuntimeException("Should have been overriden by mixin.");
    }

    default int sophisticatedLibrary_getGuiTop() {
        throw new RuntimeException("Should have been overriden by mixin.");
    }

    @Nullable
    default Slot sophisticatedLibrary_getSlotUnderMouse() {
        throw new RuntimeException("Should have been overriden by mixin.");
    }
}
