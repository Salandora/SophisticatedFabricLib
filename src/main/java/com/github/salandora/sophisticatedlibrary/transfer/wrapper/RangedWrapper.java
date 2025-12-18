package com.github.salandora.sophisticatedlibrary.transfer.wrapper;

import com.github.salandora.sophisticatedlibrary.transfer.IItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class RangedWrapper implements IItemHandlerModifiable {
	private final List<SingleSlotStorage<ItemVariant>> parts;
	private final IItemHandlerModifiable inventory;
	private final int start;
	private final int end;

	public RangedWrapper(IItemHandlerModifiable inventory, int start, int end) {
		this.inventory = inventory;
		this.start = start;
		this.end = end;

		this.parts = Collections.unmodifiableList(this.inventory.getSlots().subList(start, end));
	}

	@Override
	@UnmodifiableView
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return this.parts;
	}

	@Override
	public int getSlotCount() {
		return end - start;
	}

	private boolean validateSlotIndex(int slot) {
		return start + slot < end;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (!validateSlotIndex(slot)) {
			return;
		}

		this.inventory.setStackInSlot(start + slot, stack);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (!validateSlotIndex(slot)) {
			return ItemStack.EMPTY;
		}

		return this.inventory.getStackInSlot(start + slot);
	}

	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (!validateSlotIndex(slot)) {
			return stack;
		}

		return this.inventory.insertItem(start + slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (!validateSlotIndex(slot)) {
			return ItemStack.EMPTY;
		}

		return this.inventory.extractItem(start + slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		if (!validateSlotIndex(slot)) {
			return 0;
		}

		return this.inventory.getSlotLimit(start + slot);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (!validateSlotIndex(slot)) {
			return false;
		}

		return this.inventory.isItemValid(start + slot, stack);
	}
}