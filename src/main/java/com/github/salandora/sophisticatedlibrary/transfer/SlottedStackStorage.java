package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface SlottedStackStorage extends SlottedStorage<ItemVariant> {
	ItemStack getStackInSlot(int slot);

	int getSlotLimit(int slot);

	default boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	default long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		TransactionCallback.onSuccess(ctx, () -> insertItem(slot, resource.toStack((int) maxAmount), false));
		return maxAmount - insertItem(slot, resource.toStack((int) maxAmount), true).getCount();
	}

	default long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		if (!resource.matches(getStackInSlot(slot))){
			return 0;
		}

		TransactionCallback.onSuccess(ctx, () -> extractItem(slot, (int) maxAmount, false));
		return extractItem(slot, (int) maxAmount, true).getCount();
	}

	@Override
	default Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}

	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	ItemStack extractItem(int slot, int amount, boolean simulate);
}
