package com.github.salandora.sophisticatedlibrary.transfer.api.v1;

import net.minecraft.world.item.ItemStack;

public class EmptyItemHandler implements IItemHandlerModifiable {
	public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public int getSlotCount() {
		return 0;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {	return false; }
}
