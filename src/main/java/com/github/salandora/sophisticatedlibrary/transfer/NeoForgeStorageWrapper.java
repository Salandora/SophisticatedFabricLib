/*
package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class NeoForgeStorageWrapper implements IItemHandler {
	private final Storage<ItemVariant> storage;

	private NeoForgeStorageWrapper(Storage<ItemVariant> storage) {
		this.storage = storage;
	}

	@Override
	public int getSlots() {
		if (this.storage instanceof SlottedStorage<ItemVariant> slotted) {
			return slotted.getSlotCount();
		}

		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		throw new NotImplementedException();
	}

	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		int inserted;
		if (this.storage instanceof SlottedStorage<ItemVariant> slotted) {
			try (Transaction insert = Transaction.openOuter()) {
				inserted = (int) slotted.getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), insert);
				if (!simulate) {
					insert.commit();
				}
			}
			return stack.copyWithCount(stack.getCount() - inserted);
		}

		try (Transaction insert = Transaction.openOuter()) {
			inserted = (int) this.storage.insert(ItemVariant.of(stack), stack.getCount(), insert);
			if (!simulate) {
				insert.commit();
			}
		}
		return stack.copyWithCount(stack.getCount() - inserted);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.storage instanceof SlottedStorage<ItemVariant> slotted) {
			var s = slotted.getSlot(slot);
			int extracted;
			try (Transaction extract = Transaction.openOuter()) {
				extracted = (int) s.extract(s.getResource(), amount, extract);
				if (!simulate) {
					extract.commit();
				}
			}
			return s.getResource().toStack(extracted);
		}

		ResourceAmount<ItemVariant> resourceAmount;
		try (Transaction extract = Transaction.openOuter()) {
			resourceAmount =  StorageUtil.extractAny(this.storage, amount, extract);
			if (!simulate) {
				extract.commit();
			}
		}
		return resourceAmount.resource().toStack((int) resourceAmount.amount());
	}

	@Override
	public int getSlotLimit(int slot) {
		if (this.storage instanceof SlottedStorage<ItemVariant> slotted) {
			return (int) slotted.getSlot(slot).getCapacity();
		}

		var resource = StorageUtil.findStoredResource(this.storage);
		return resource != null ? resource.getItem().getDefaultMaxStackSize() : 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}
}
*/
