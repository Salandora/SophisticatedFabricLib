/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.20.1/src/main/java/net/minecraftforge/items/wrapper/PlayerOffhandInvWrapper.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class PlayerOffhandInvWrapper extends RangedWrapper {
    public static PlayerOffhandInvWrapper of(Player player) {
        return new PlayerOffhandInvWrapper(player.getInventory());
    }
    public static PlayerOffhandInvWrapper of(Inventory container) {
        return new PlayerOffhandInvWrapper(container);
    }

    private PlayerOffhandInvWrapper(Inventory inv) {
        super(InvWrapper.of(inv), Inventory.SLOT_OFFHAND, Inventory.SLOT_OFFHAND + 1);
    }
}