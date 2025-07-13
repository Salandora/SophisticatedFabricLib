package com.github.salandora.sophisticatedlibrary.common.extensions.item;

import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.world.item.ItemStack;

public interface SophisticatedItemStackExtension {
	private ItemStack self() {
		return (ItemStack) this;
	}

	default boolean sophisticatedLibrary$canPerformAction(ItemAbility itemAbility) {
		return self().getItem().sophisticatedLibrary$canPerformAction(self(), itemAbility);
	}
}
