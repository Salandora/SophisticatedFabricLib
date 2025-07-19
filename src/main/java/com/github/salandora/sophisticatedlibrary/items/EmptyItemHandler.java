package com.github.salandora.sophisticatedlibrary.items;

import com.github.salandora.sophisticatedlibrary.transfer.IItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
	public int getSlots() {
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
