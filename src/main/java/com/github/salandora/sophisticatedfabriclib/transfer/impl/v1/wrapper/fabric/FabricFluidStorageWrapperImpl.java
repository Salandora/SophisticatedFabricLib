package com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class FabricFluidStorageWrapperImpl implements FabricFluidStorageWrapper {
	public static FabricFluidStorageWrapper of(Storage<FluidVariant> storage, @Nullable ContainerItemContext context) {
		return new FabricFluidStorageWrapperImpl(storage, context);
	}

	private final Storage<FluidVariant> storage;
	@Nullable
	private final ContainerItemContext container;

	public FabricFluidStorageWrapperImpl(Storage<FluidVariant> storage, @Nullable ContainerItemContext container) {
		this.storage = storage;
		this.container = container;
	}

	@Nullable
	@Override
	public ItemStack getContainer() {
		return this.container.getItemVariant().toStack((int) this.container.getAmount());
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		ResourceAmount<FluidVariant> resource = StorageUtil.findExtractableContent(storage, null);
		if (resource == null || resource.resource().isBlank()) {
			return FluidStack.EMPTY;
		}
		return new FluidStack(resource);
	}

	@Override
	public void setFluidInTank(int tank, FluidStack fluidStack) {
		// noop
	}

	@Override
	public long getTankCapacity(int tank) {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack fluidStack) {
		return true;
	}

	@Override
	public long fill(FluidStack fluidStack, FluidAction action) {
		if (fluidStack.isEmpty()) {
			return 0;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long inserted = storage.insert(fluidStack.getVariant(), fluidStack.getAmount(), ctx);
			if (action.execute()) {
				ctx.commit();
			}

			return inserted;
		}
	}

	@Override
	public FluidStack drain(FluidStack drainStack, FluidAction action) {
		if (drainStack.isEmpty()) {
			return FluidStack.EMPTY;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long extracted = storage.extract(drainStack.getVariant(), drainStack.getAmount(), ctx);
			if (action.execute()) {
				ctx.commit();
			}

			return drainStack.copyWithAmount(extracted);
		}
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action) {
		FluidVariant variant = StorageUtil.findStoredResource(storage);
		if (variant == null || variant.isBlank()) {
			return FluidStack.EMPTY;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long extracted = storage.extract(variant, maxDrain, ctx);
			if (action.execute()) {
				ctx.commit();
			}

			return new FluidStack(variant, extracted);
		}
	}
}
