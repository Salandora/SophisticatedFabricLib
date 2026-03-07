/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.20.1/src/main/java/net/minecraftforge/items/wrapper/RangedWrapper.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;

public class RangedWrapper implements IItemHandlerModifiable {
	private final IItemHandlerModifiable inventory;
	private final int start;
	private final int end;

	public RangedWrapper(IItemHandlerModifiable inventory, int start, int end) {
		this.inventory = inventory;
		this.start = start;
		this.end = end;
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
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
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