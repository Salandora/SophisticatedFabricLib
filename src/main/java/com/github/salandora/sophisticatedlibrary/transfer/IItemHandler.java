package com.github.salandora.sophisticatedlibrary.transfer;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemHandler {
	int getSlots();

	ItemStack getStackInSlot(int slot);

	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, ItemStack stack);
}
