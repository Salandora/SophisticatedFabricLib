package com.github.salandora.sophisticatedfabriclib.transfer.api.v1;

import net.minecraft.world.item.ItemStack;

public interface IItemHandler {
	int getSlotCount();

	ItemStack getStackInSlot(int slot);

	void setStackInSlot(int slot, ItemStack stack);

	default long insert(ItemStack stack, boolean simulate) {
		ItemStack remaining = stack;
		for (var slot = 0; slot < getSlotCount() && !remaining.isEmpty(); slot++) {
			remaining = insertItem(slot, remaining, simulate);
		}

		return stack.getCount() - remaining.getCount();
	}

	ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

	default long extract(ItemStack stack, boolean simulate) {
		int remaining = stack.getCount();
		for (var slot = 0; slot < getSlotCount() && remaining > 0; slot++) {
			ItemStack slotStack = getStackInSlot(slot);
			if (slotStack.isEmpty() || !ItemStack.isSameItemSameComponents(slotStack, stack)) {
				continue;
			}

			remaining -= extractItem(slot, remaining, simulate).getCount();
		}

		return stack.getCount() - remaining;
	}

	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, ItemStack stack);
}
