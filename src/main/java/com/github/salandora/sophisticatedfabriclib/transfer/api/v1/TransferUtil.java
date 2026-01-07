package com.github.salandora.sophisticatedfabriclib.transfer.api.v1;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface TransferUtil {
	static ItemStack insertItemStacked(IItemHandler storage, ItemStack stack, boolean simulate) {
		if (storage == null || stack.isEmpty()) {
			return stack;
		}

		if (!stack.isStackable()) {
			return stack.copyWithCount(stack.getCount() - (int) storage.insert(stack, simulate));
		}

		int storageSize = storage.getSlotCount();

		// fill up stacks
		for (int slot = 0; slot < storageSize; slot++) {
			ItemStack slotStack = storage.getStackInSlot(slot);
			if (ItemStack.isSameItemSameComponents(stack, slotStack)) {
				stack = storage.insertItem(slot, stack, simulate);
				if (stack.isEmpty()) {
					return stack;
				}
			}
		}

		// deal with remainder
		for (int slot = 0; slot < storageSize; slot++) {
			if (storage.getStackInSlot(slot).isEmpty()) {
				stack = storage.insertItem(slot, stack, simulate);
				if (stack.isEmpty()) {
					return stack;
				}
			}
		}

		return stack;
	}

	static void giveOrDropToPlayer(Player player, ItemStack stack) {
		var added = player.getInventory().add(stack);
		if (added && stack.isEmpty()) {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			player.getInventory().setChanged();
		} else {
			var entity = player.drop(stack, false, false);
			if (entity != null) {
				entity.setNoPickUpDelay();
				entity.setTarget(player.getUUID());
			}
		}
	}
}
