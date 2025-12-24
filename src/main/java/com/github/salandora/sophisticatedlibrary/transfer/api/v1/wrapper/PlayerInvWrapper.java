package com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class PlayerInvWrapper extends CombinedInvWrapper {
    public static PlayerInvWrapper of(Player player) {
        return new PlayerInvWrapper(player.getInventory());
    }
    public static PlayerInvWrapper of(Inventory container) {
        return new PlayerInvWrapper(container);
    }

    private PlayerInvWrapper(Inventory inv) {
        super(PlayerMainInvWrapper.of(inv), PlayerArmorInvWrapper.of(inv), PlayerOffhandInvWrapper.of(inv));
    }
}