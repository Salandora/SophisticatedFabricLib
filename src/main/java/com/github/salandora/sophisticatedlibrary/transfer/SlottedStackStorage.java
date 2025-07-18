package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface SlottedStackStorage extends SlottedStorage<ItemVariant> {
	ItemStack getStackInSlot(int slot);

	void setStackInSlot(int slot, ItemStack stack);

	int getSlotLimit(int slot);

	default boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	default long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return getSlot(slot).insert(resource, maxAmount, ctx);
	}

	default long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return getSlot(slot).extract(resource, maxAmount, ctx);
	}

	@Override
	default Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}

	/// Do not call from an open transaction
	default ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		long inserted;
		try (Transaction ctx = Transaction.openOuter()) {
			inserted = getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), ctx);
			if (!simulate) {
				ctx.commit();
			}
		}
		return inserted < stack.getCount() ? stack.copyWithCount(stack.getCount() - (int) inserted) : ItemStack.EMPTY;
	}

	@NotNull
	/// Do not call from an open transaction
	default ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
		long inserted;
		try (Transaction ctx = Transaction.openOuter()) {
			inserted = insert(ItemVariant.of(stack), stack.getCount(), ctx);
			if (!simulate) {
				ctx.commit();
			}
		}
		return inserted < stack.getCount() ? stack.copyWithCount(stack.getCount() - (int) inserted) : ItemStack.EMPTY;
	}

	@NotNull
	/// Do not call from an open transaction
	default ItemStack extractItem(int slot, int amount, boolean simulate) {
		var slotStorage = getSlot(slot);
		ItemVariant resource = slotStorage.getResource();
		long extracted;
		try (Transaction outer = Transaction.openOuter()) {
			extracted = slotStorage.extract(resource, amount, outer);
			if (!simulate) {
				outer.commit();
			}
		}
		return resource.toStack((int) extracted);
	}
}
