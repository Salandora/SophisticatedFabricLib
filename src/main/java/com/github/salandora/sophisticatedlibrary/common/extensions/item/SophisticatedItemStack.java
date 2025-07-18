package com.github.salandora.sophisticatedlibrary.common.extensions.item;

import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.world.item.ItemStack;

public interface SophisticatedItemStack {
	private ItemStack self() {
		return (ItemStack) this;
	}

	default boolean sophisticatedLibrary_canPerformAction(ItemAbility itemAbility) {
		return self().getItem().sophisticatedLibrary$canPerformAction(self(), itemAbility);
	}
}
