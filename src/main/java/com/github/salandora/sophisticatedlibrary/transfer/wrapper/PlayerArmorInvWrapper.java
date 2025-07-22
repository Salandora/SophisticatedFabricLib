package com.github.salandora.sophisticatedlibrary.transfer.wrapper;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerArmorInvWrapper extends RangedWrapper {
    public static PlayerArmorInvWrapper of(Player player) {
        return new PlayerArmorInvWrapper(player.getInventory());
    }
    public static PlayerArmorInvWrapper of(Inventory container) {
        return new PlayerArmorInvWrapper(container);
    }

    private final Inventory inventoryPlayer;

    private PlayerArmorInvWrapper(Inventory inv) {
        super(InvWrapper.of(inv), Inventory.INVENTORY_SIZE, Inventory.INVENTORY_SIZE + 4);
        inventoryPlayer = inv;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        EquipmentSlot equ = null;
        for (EquipmentSlot s : EquipmentSlot.values()) {
            if (s.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && s.getIndex() == slot) {
                equ = s;
                break;
            }
        }
        // check if it's valid for the armor slot
        if (equ != null && slot < 4 && !stack.isEmpty() && getInventoryPlayer().player.getEquipmentSlotForItem(stack) == equ) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public Inventory getInventoryPlayer() {
        return inventoryPlayer;
    }
}