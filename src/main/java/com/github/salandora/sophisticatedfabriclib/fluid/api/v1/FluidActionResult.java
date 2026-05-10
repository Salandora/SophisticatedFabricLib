package com.github.salandora.sophisticatedfabriclib.fluid.api.v1;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;

public record FluidActionResult(boolean success, ItemVariant variant, long count) {
	public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemVariant.blank(), 0);

	public FluidActionResult(ItemStack result) {
		this(true, result);
	}

	public FluidActionResult(boolean success, ItemStack result) {
		this(success, ItemVariant.of(result), result.getCount());
	}

	public boolean isSuccess() {
		return success;
	}

	public ItemStack getResult() {
		return variant.toStack((int) count);
	}
}
