package com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.items;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandlerModifiable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShulkerInvWrapper implements IItemHandlerModifiable {
	private final ItemStack shulkerBox;

	private CompoundTag cachedTag;
	private NonNullList<ItemStack> cachedStacks;

	public ShulkerInvWrapper(ItemStack shulkerBox) {
		this.shulkerBox = shulkerBox;
	}

	private NonNullList<ItemStack> getShulkerInv() {
		CompoundTag tag = BlockItem.getBlockEntityData(this.shulkerBox);
		if (cachedTag == null || !cachedTag.equals(tag)) {
			cachedTag = tag;
			cachedStacks = NonNullList.withSize(getSlotCount(), ItemStack.EMPTY);
			if (tag != null && tag.contains("Items", CompoundTag.TAG_LIST)) {
				ContainerHelper.loadAllItems(tag, cachedStacks);
			}
		}
		return cachedStacks;
	}

	private void setShulkerInv(NonNullList<ItemStack> stacks) {
		CompoundTag tag = BlockItem.getBlockEntityData(this.shulkerBox);
		CompoundTag savedTag = ContainerHelper.saveAllItems(tag == null ? new CompoundTag() : tag, stacks);
		BlockItem.setBlockEntityData(this.shulkerBox, BlockEntityType.SHULKER_BOX, savedTag);
		cachedTag = savedTag;
	}

	@Override
	public int getSlotCount() {
		return 27;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems();
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= getSlotCount())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlotCount() + ")");
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return getShulkerInv().get(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (!isItemValid(slot, stack)) {
			throw new RuntimeException("Invalid stack " + stack + " for slot " + slot);
		}

		NonNullList<ItemStack> stacks = getShulkerInv();
		stacks.set(slot, stack);
		setShulkerInv(stacks);
	}

	protected int getStackLimit(int slot, ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);
		if (!isItemValid(slot, stack)) {
			return stack;
		}

		NonNullList<ItemStack> stacks = getShulkerInv();

		ItemStack existing = stacks.get(slot);
		int limit = getStackLimit(slot, stack);
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameTags(stack, existing)) {
				return stack;
			}

			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			setShulkerInv(stacks);
		}

		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		NonNullList<ItemStack> stacks = getShulkerInv();

		ItemStack existing = stacks.get(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int extract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= extract) {
			if (!simulate) {
				stacks.set(slot, ItemStack.EMPTY);
				setShulkerInv(stacks);
				return existing;
			} else {
				return existing.copy();
			}
		}

		if (!simulate) {
			stacks.set(slot, existing.copyWithCount(existing.getCount() - extract));
			setShulkerInv(stacks);
		}

		return existing.copyWithCount(extract);
	}
}
