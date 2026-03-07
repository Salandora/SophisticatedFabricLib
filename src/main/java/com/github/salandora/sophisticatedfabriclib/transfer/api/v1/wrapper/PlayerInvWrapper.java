/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.20.1/src/main/java/net/minecraftforge/items/wrapper/PlayerInvWrapper.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper;

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