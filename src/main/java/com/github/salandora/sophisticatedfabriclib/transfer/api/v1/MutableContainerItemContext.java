package com.github.salandora.sophisticatedfabriclib.transfer.api.v1;

import com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.MutableContainerItemContextImpl;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.item.ItemStack;

public interface MutableContainerItemContext extends ContainerItemContext {
	static MutableContainerItemContext ofSingleStack(ItemStack stack) {
		return new MutableContainerItemContextImpl(stack);
	}
}
