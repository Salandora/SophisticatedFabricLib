package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface SophisticatedShearable {
	default boolean sophisticatedFabricLibrary_isShearable(ItemStack item, Level level, BlockPos pos) {
		// Default to checking readyForShearing if we are the vanilla shearable interface, and if we aren't assume a default of true
		return !(this instanceof Shearable shearable) || shearable.readyForShearing();
	}
}
