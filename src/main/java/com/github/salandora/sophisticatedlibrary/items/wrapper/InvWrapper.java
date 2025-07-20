package com.github.salandora.sophisticatedlibrary.items.wrapper;

import com.github.salandora.sophisticatedlibrary.transfer.IItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InvWrapper implements IItemHandlerModifiable {
	public static InvWrapper of(Container container) {
		return new InvWrapper(container);
	}

	private final Container wrappedInventory;
	private final InventoryStorage wrappedInventoryStorage;

	private InvWrapper(Container inventory) {
		this.wrappedInventory = inventory;
		this.wrappedInventoryStorage = InventoryStorage.of(inventory, null);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		ItemStack stackInSlot = this.wrappedInventory.getItem(slot);

		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)) ||
					!ItemStack.isSameItemSameComponents(stack, stackInSlot) ||
					!this.wrappedInventory.canPlaceItem(slot, stack)) {
				return stack;
			}

			int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();
			if (stack.getCount() <= limit) {
				if (!simulate) {
					ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					this.wrappedInventory.setItem(slot, copy);
					this.wrappedInventory.setChanged();
				}

				return ItemStack.EMPTY;
			} else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					ItemStack copy = stack.split(limit);
					copy.grow(stackInSlot.getCount());
					this.wrappedInventory.setItem(slot, copy);
					this.wrappedInventory.setChanged();
					return stack;
				} else {
					stack.shrink(limit);
					return stack;
				}
			}
		} else {
			if (!this.wrappedInventory.canPlaceItem(slot, stack)) {
				return stack;
			}

			int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (limit < stack.getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					this.wrappedInventory.setItem(slot, stack.split(limit));
					this.wrappedInventory.setChanged();
					return stack;
				} else {
					stack.shrink(limit);
					return stack;
				}
			} else {
				if (!simulate) {
					this.wrappedInventory.setItem(slot, stack);
					this.wrappedInventory.setChanged();
				}
				return ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;

		ItemStack stackInSlot = this.wrappedInventory.getItem(slot);

		if (stackInSlot.isEmpty())
			return ItemStack.EMPTY;

		if (simulate) {
			if (stackInSlot.getCount() < amount) {
				return stackInSlot.copy();
			} else {
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		} else {
			int m = Math.min(stackInSlot.getCount(), amount);

			ItemStack extracted = this.wrappedInventory.removeItem(slot, m);
			this.wrappedInventory.setChanged();
			return extracted;
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.wrappedInventory.getMaxStackSize();
	}

	@Override
	public int getSlotCount() {
		return this.wrappedInventory.getContainerSize();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return wrappedInventory.getItem(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		this.wrappedInventory.setItem(slot, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return this.wrappedInventory.canPlaceItem(slot, stack);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		InvWrapper other = (InvWrapper) o;
		return this.wrappedInventory.equals(other.wrappedInventory);
	}

	@Override
	public int hashCode() {
		return this.wrappedInventory.hashCode();
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.wrappedInventoryStorage.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.wrappedInventoryStorage.extract(resource, maxAmount, transaction);
	}
}
