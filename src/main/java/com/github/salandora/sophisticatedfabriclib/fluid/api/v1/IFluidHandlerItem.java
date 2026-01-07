package com.github.salandora.sophisticatedfabriclib.fluid.api.v1;

import net.minecraft.world.item.ItemStack;

public interface IFluidHandlerItem extends IFluidHandler {
	ItemStack getContainer();
}
