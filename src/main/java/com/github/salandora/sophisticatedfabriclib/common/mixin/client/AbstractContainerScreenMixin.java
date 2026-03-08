package com.github.salandora.sophisticatedfabriclib.common.mixin.client;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.client.gui.screens.inventory.SophisticatedAbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements SophisticatedAbstractContainerScreen {
	@Shadow
	@Nullable
	protected Slot hoveredSlot;

	@Shadow protected int leftPos;

	@Shadow protected int topPos;

	@Shadow protected int imageWidth;

	@Override
	public int sophisticatedFabricLibrary_getXSize() {
		return imageWidth;
	}

	@Override
	public int sophisticatedFabricLibrary_getGuiLeft() {
		return leftPos;
	}

	@Override
	public int sophisticatedFabricLibrary_getGuiTop() {
		return topPos;
	}

	@Override
	@Nullable
	public Slot sophisticatedFabricLibrary_getSlotUnderMouse() {
		return hoveredSlot;
	}
}
