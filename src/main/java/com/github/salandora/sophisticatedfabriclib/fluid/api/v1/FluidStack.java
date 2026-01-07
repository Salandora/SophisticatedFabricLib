package com.github.salandora.sophisticatedfabriclib.fluid.api.v1;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public final class FluidStack extends SingleFluidStorage {
	public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

	public FluidStack(StorageView<FluidVariant> view) {
		this(view.getResource(), view.getAmount());
	}

	public FluidStack(ResourceAmount<FluidVariant> resource) {
		this(resource.resource(), resource.amount());
	}

	public FluidStack(Fluid fluid, long amount) {
		this(FluidVariant.of(fluid), amount);
	}

	public FluidStack(FluidStack stack, long amount) {
		this(stack.getVariant(), amount);
	}

	public FluidStack(FluidVariant variant, long amount) {
		this.variant = variant;
		this.amount = amount;
	}

	private FluidStack() {
	}

	public Component getDisplayName() {
		return FluidVariantAttributes.getName(this.variant);
	}

	@Override
	protected long getCapacity(FluidVariant variant) {
		return Long.MAX_VALUE;
	}

	public Fluid getFluid() {
		return this.variant.getFluid();
	}

	public FluidVariant getVariant() {
		return this.variant;
	}

	public boolean isEmpty() {
		return this.isResourceBlank();
	}

	public FluidStack copy() {
		return new FluidStack(this.variant, this.amount);
	}

	public FluidStack copyWithAmount(long amount) {
		return new FluidStack(this.variant, amount);
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		if (this.isEmpty()) {
			return tag;
		}

		writeNbt(tag);
		return tag;
	}

	public static FluidStack loadFluidStackFromNBT(CompoundTag tag) {
		if (tag.isEmpty()) {
			return EMPTY;
		}
		FluidStack stack = new FluidStack();
		stack.readNbt(tag);
		return stack;
	}

	public boolean is(TagKey<Fluid> tag) {
		return this.variant.getFluid().is(tag);
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public void grow(long added) {
		this.setAmount(getAmount() + added);
	}

	public void shrink(long removed) {
		this.grow(-removed);
	}

	public boolean isFluidEqual(@NotNull FluidStack other) {
		if (this == other) {
			return true;
		}

		return isFluidEqual(other.getVariant());
	}

	public boolean isFluidEqual(FluidVariant other) {
		return isFluidEqual(getVariant(), other);
	}

	public static boolean isFluidEqual(FluidVariant a, FluidVariant b) {
		if (a == b) {
			return true;
		}

		if (b == null) {
			return false;
		}

		return a.isOf(b.getFluid()) && a.getNbt().equals(b.getNbt());
	}

	@Override
	public int hashCode() {
		long code = 1;
		code = 31 * code + getFluid().hashCode();
		code = 31 * code + amount;
		if (variant.hasNbt()) {
			code = 31 * code + variant.getNbt().hashCode();
		}

		return (int) code;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FluidStack other)) {
			return false;
		}
		return isFluidEqual(other);
	}
}
