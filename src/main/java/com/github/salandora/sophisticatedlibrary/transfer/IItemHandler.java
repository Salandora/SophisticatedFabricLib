package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface IItemHandler extends SlottedStorage<ItemVariant> {
	int getSlotCount();

	ItemStack getStackInSlot(int slot);

	void setStackInSlot(int slot, ItemStack stack);

	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, ItemStack stack);

	@Override
	default Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}

	@Override
	default SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return new SingleStackStorage() {
			@Override
			protected ItemStack getStack() {
				return getStackInSlot(slot);
			}

			@Override
			protected void setStack(ItemStack stack) {
				setStackInSlot(slot, stack);
			}
		};
	}

	/// Do not override
	@Override
	default long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		ItemStack remaining = resource.toStack((int) maxAmount);
		for (int i = 0; i < getSlotCount(); i++) {
			final int index = i;
			final ItemStack current = remaining;
			remaining = insertItem(index, remaining, true);
			TransactionCallback.onSuccess(transaction, () -> insertItem(index, current, false));
			if (remaining.isEmpty()) {
				break;
			}
		}
		return maxAmount - remaining.getCount();
	}

	/// Do not override
	@Override
	default long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		int remaining = (int) maxAmount;
		for (int i = 0; i < getSlotCount(); i++) {
			final int index = i;
			final int current = remaining;
			remaining -= extractItem(i, remaining, true).getCount();
			TransactionCallback.onSuccess(transaction, () -> extractItem(index, current, false));
			if (remaining >= maxAmount) {
				break;
			}
		}
		return maxAmount - remaining;
	}
}
