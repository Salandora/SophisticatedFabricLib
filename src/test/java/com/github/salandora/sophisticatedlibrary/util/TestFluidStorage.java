package com.github.salandora.sophisticatedlibrary.util;

import com.github.salandora.sophisticatedlibrary.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedlibrary.fluid.api.v1.IFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class TestFluidStorage implements IFluidHandler {
	private final long capacity;
	private FluidStack content = FluidStack.EMPTY;

	public TestFluidStorage(long capacity) {
		this.capacity = capacity;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.content;
	}

	@Override
	public void setFluidInTank(int tank, FluidStack fluidStack) {
		this.content = fluidStack;
	}

	public long getAmount() {
		return this.content.getAmount();
	}

	@Override
	public long getTankCapacity(int tank) {
		return this.capacity;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack fluidStack) {
		return true;
	}

	public void insertTestFluid(FluidVariant variant, long amount) {
		this.content = new FluidStack(variant, amount);
	}

	@Override
	public long fill(FluidStack fluidStack, FluidAction action) {
		if (fluidStack.isEmpty()) {
			return 0;
		}

		if (this.content.isEmpty()) {
			long inserted = Math.min(fluidStack.getAmount(), this.capacity);
			if (action.execute()) {
				this.content = fluidStack.copyWithAmount(inserted);
			}
			return inserted;
		}

		if (this.content.isFluidEqual(fluidStack)) {
			long inserted = Math.min(fluidStack.getAmount(), this.capacity - this.content.getAmount());
			if (action.execute()) {
				this.content.grow(inserted);
			}

			return inserted;
		}

		return 0;
	}

	@Override
	public FluidStack drain(FluidStack drainStack, FluidAction action) {
		if (this.content.isEmpty()) {
			return FluidStack.EMPTY;
		}

		if (this.content.isFluidEqual(drainStack)) {
			long extracted = Math.min(drainStack.getAmount(), this.capacity - this.content.getAmount());
			if (action.execute()) {
				this.content.shrink(extracted);
			}

			return drainStack.copyWithAmount(extracted);
		}

		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action) {
		return drain(this.content.copy(), action);
	}
}