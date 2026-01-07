package com.github.salandora.sophisticatedfabriclib.event.api.v0.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface PlayerEvents {
	Event<PlayerEvents.ItemCrafted> ITEM_CRAFTED = EventFactory.createArrayBacked(PlayerEvents.ItemCrafted.class, callbacks -> (player, stack, craftMatrix) -> {
		for (PlayerEvents.ItemCrafted callback : callbacks)
			callback.onItemCrafted(player, stack, craftMatrix);
	});

	@FunctionalInterface
	interface ItemCrafted {
		void onItemCrafted(Player player, ItemStack stack, Container craftMatrix);
	}
}
