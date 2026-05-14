package com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

public class FabricItemStorageWrapperImpl implements FabricItemStorageWrapper {
	public static FabricItemStorageWrapper of(Storage<ItemVariant> storage) {
		if (storage instanceof SlottedStorage<ItemVariant> slotted) {
			return new SlottedWrapperImpl(slotted);
		}
		return new FabricItemStorageWrapperImpl(storage);
	}

	private final Storage<ItemVariant> storage;

	public FabricItemStorageWrapperImpl(Storage<ItemVariant> storage) {
		this.storage = storage;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		SophisticatedFabricLib.LOGGER.error("Unable to set stack in slot {}: {}", slot, stack);
	}

	@Override
	public long insert(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return 0;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), ctx);
			if (!simulate) {
				ctx.commit();
			}

			return (int) inserted;
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public long extract(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return 0;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long extracted = storage.extract(ItemVariant.of(stack), stack.getCount(), ctx);
			if (!simulate) {
				ctx.commit();
			}

			return (int) extracted;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	public static class SlottedWrapperImpl extends FabricItemStorageWrapperImpl {
		private final SlottedStorage<ItemVariant> slotted;

		public SlottedWrapperImpl(SlottedStorage<ItemVariant> slotted) {
			super(slotted);
			this.slotted = slotted;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.isEmpty()) {
				return stack;
			}

			try (Transaction ctx = Transaction.openOuter()) {
				long inserted = slotted.getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), ctx);
				if (!simulate) {
					ctx.commit();
				}

				return stack.copyWithCount((int) (stack.getCount() - inserted));
			}
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (amount == 0) {
				return ItemStack.EMPTY;
			}

			var resource = slotted.getSlot(slot).getResource();
			if (resource.isBlank()) {
				return ItemStack.EMPTY;
			}

			try (Transaction ctx = Transaction.openOuter()) {
				long extracted = slotted.getSlot(slot).extract(resource, amount, ctx);
				if (!simulate) {
					ctx.commit();
				}

				return resource.toStack((int) extracted);
			}
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			var s = slotted.getSlot(slot);
			return s.getResource().toStack((int) s.getAmount());
		}

		@Override
		public int getSlotCount() {
			return slotted.getSlotCount();
		}

		@Override
		public int getSlotLimit(int slot) {
			return (int) slotted.getSlot(slot).getCapacity();
		}
	}
}
