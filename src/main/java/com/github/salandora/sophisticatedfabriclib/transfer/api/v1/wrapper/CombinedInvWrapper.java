/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.21.1/src/main/java/net/neoforged/neoforge/items/wrapper/CombinedInvWrapper.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.EmptyItemHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;

public class CombinedInvWrapper implements IItemHandlerModifiable {
    protected final IItemHandlerModifiable[] itemHandler; // the handlers
    protected final int[] baseIndex;
    protected final int slotCount;

    public CombinedInvWrapper(IItemHandlerModifiable... itemHandler) {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++) {
            index += itemHandler[i].getSlotCount();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot) {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        if (index < 0 || index >= itemHandler.length) {
            return EmptyItemHandler.INSTANCE;
        }
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= baseIndex.length) {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }
}
