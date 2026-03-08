package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component.SophisticatedItemContainerContents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemContainerContents.class)
public class ItemContainerContentsMixin implements SophisticatedItemContainerContents {
	@Shadow
	@Final
	private NonNullList<ItemStack> items;

	@Override
	public int sophisticatedFabricLibrary_getSlots() {
		return this.items.size();
	}

	@Override
	public ItemStack sophisticatedFabricLibrary_getStackInSlot(int slot) {
		this.sophisticatedFabricLibrary_validateSlotIndex(slot);
		return this.items.get(slot).copy();
	}

	@Unique
	private void sophisticatedFabricLibrary_validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.sophisticatedFabricLibrary_getSlots()) {
			throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + this.sophisticatedFabricLibrary_getSlots() + ")");
		}
	}
}
