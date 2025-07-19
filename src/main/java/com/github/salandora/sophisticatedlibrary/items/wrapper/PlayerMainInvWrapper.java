package com.github.salandora.sophisticatedlibrary.items.wrapper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerMainInvWrapper extends RangedWrapper {
	public static PlayerMainInvWrapper of(Player player) {
		return new PlayerMainInvWrapper(player.getInventory());
	}
	public static PlayerMainInvWrapper of(Inventory container) {
		return new PlayerMainInvWrapper(container);
	}

	private final Inventory inventoryPlayer;

	private PlayerMainInvWrapper(Inventory inv) {
		super(InvWrapper.of(inv), 0, inv.items.size());
		this.inventoryPlayer = inv;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack rest = super.insertItem(slot, stack, simulate);
		if (rest.getCount() != stack.getCount()) {
			// the stack in the slot changed, animate it
			ItemStack inSlot = getStackInSlot(slot);
			if (!inSlot.isEmpty()) {
				if (getInventoryPlayer().player.level().isClientSide) {
					inSlot.setPopTime(5);
				} else if (getInventoryPlayer().player instanceof ServerPlayer) {
					getInventoryPlayer().player.containerMenu.broadcastChanges();
				}
			}
		}
		return rest;
	}

	public Inventory getInventoryPlayer() {
		return this.inventoryPlayer;
	}
}