package com.github.salandora.sophisticatedlibrary.inventory;

import com.github.salandora.sophisticatedlibrary.transfer.api.v1.IItemHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final IItemHandler itemHandler;
	private final int index;

	public SlotItemHandler(IItemHandler slottedStorage, int slot, int x, int y) {
		super(emptyInventory, slot, x, y);
		this.itemHandler = slottedStorage;
		this.index = slot;
	}

	public IItemHandler getItemHandler() {
		return itemHandler;
	}

	@Override
	public ItemStack getItem() {
		return getItemHandler().getStackInSlot(this.index);
	}

	@Override
	public void set(ItemStack stack) {
		getItemHandler().setStackInSlot(this.index, stack);
		this.setChanged();
	}

	@Override
	public ItemStack remove(int amount) {
		return this.getItemHandler().extractItem(index, amount, false);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		return this.getItemHandler().isItemValid(index, stack);
	}

	@Override
	public boolean mayPickup(Player player) {
		return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
	}

	@Override
	public int getMaxStackSize() {
		return this.getItemHandler().getSlotLimit(this.index);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(stack.getMaxStackSize(), this.getItemHandler().getSlotLimit(this.index));
	}

	@Override
	public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
	}
}
