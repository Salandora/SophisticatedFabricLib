package com.github.salandora.sophisticatedlibrary.common.extensions.item;

import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.world.item.ItemStack;

public interface SophisticatedItem {
	default boolean sophisticatedLibrary$canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return false;
	}
}
