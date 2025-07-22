package com.github.salandora.sophisticatedlibrary.fluid;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;

public class FluidActionResult {
	public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemVariant.blank(), 0);

	private final boolean success;
	private final ItemVariant variant;
	private final long count;

	public FluidActionResult(ItemStack result) {
		this(true, result);
	}

	public FluidActionResult(boolean success, ItemStack result) {
		this(success, ItemVariant.of(result), result.getCount());
	}

	public FluidActionResult(ItemVariant variant, long count) {
		this(true, variant, count);
	}

	public FluidActionResult(boolean success, ItemVariant variant, long count) {
		this.success = success;
		this.variant = variant;
		this.count = count;
	}

	public boolean isSuccess() {
		return success;
	}

	public ItemVariant getVariant() {
		return variant;
	}

	public long getCount() {
		return count;
	}

	public ItemStack getResult() {
		return variant.toStack((int) count);
	}
}
