package com.github.salandora.sophisticatedlibrary.transfer;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackHandler implements IItemHandlerModifiable {
	protected NonNullList<ItemStack> stacks;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public ItemStackHandler(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	public void setSize(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	public int getSlotCount() {
		return stacks.size();
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	public int getStackLimit(int slot, ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		if (!isItemValid(slot, stack)) {
			return stack;
		}

		ItemStack existing = stacks.get(slot);
		int limit = getStackLimit(slot, stack);
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(stack, existing)) {
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
			onContentsChanged(slot);
		}

		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		ItemStack existing = stacks.get(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int extract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= extract) {
			if (!simulate) {
				stacks.set(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
				return existing;
			} else {
				return existing.copy();
			}
		}

		if (!simulate) {
			stacks.set(slot, existing.copyWithCount(existing.getCount() - extract));
			onContentsChanged(slot);
		}

		return existing.copyWithCount(extract);
	}

	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		ListTag listTag = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack itemStack = stacks.get(i);
			if (!itemStack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				listTag.add(itemStack.save(registries, itemTag));
			}
		}

		CompoundTag saveTag = new CompoundTag();
		saveTag.put("Items", listTag);
		saveTag.putInt("Size", getSlotCount());
		return saveTag;
	}

	public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTag = tagList.getCompound(i);
			int slot = itemTag.getInt("Slot");
			if (slot >= 0 && slot < getSlotCount()) {
				ItemStack stack = ItemStack.parseOptional(registries, itemTag);
				stacks.set(slot, stack);
			}
		}
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	protected void onLoad() {}

	protected void onContentsChanged(int slot) {}
}
