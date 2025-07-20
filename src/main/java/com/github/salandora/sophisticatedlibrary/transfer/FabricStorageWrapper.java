package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class FabricStorageWrapper<T extends IItemHandler> implements SlottedStorage<ItemVariant> {
	public static <T extends IItemHandler> FabricStorageWrapper<T> of(T handler) {
		return new FabricStorageWrapper<>(handler);
	}

	private final T wrapped;

	private FabricStorageWrapper(T wrapped) {
		this.wrapped = wrapped;
	}

	public T getWrapped() {
		return wrapped;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long remaining = maxAmount;
		for (var slot : getSlots()) {
			remaining -= slot.insert(resource, remaining, transaction);
			if (remaining <= 0) {
				break;
			}
		}
		return maxAmount - remaining;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = 0;
		for (var slot : getSlots()) {
			extracted += slot.insert(resource, maxAmount - extracted, transaction);
			if (extracted >= maxAmount) {
				break;
			}
		}
		return extracted;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}

	@Override
	public int getSlotCount() {
		return this.wrapped.getSlotCount();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return new Slot(slot);
	}

	private class Slot extends SingleStackStorage {
		private final int slot;

		public Slot(int slot) {
			this.slot = slot;
		}

		@Override
		protected ItemStack getStack() {
			return wrapped.getStackInSlot(slot);
		}

		@Override
		protected void setStack(ItemStack stack) {
			if (wrapped instanceof IItemHandlerModifiable modifiable) {
				modifiable.setStackInSlot(this.slot, stack);
			}
		}
	}
}
