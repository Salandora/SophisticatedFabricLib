package com.github.salandora.sophisticatedlibrary.fluid.api.v1;

public class EmptyFluidHandler implements IFluidHandler {
	public static EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

	@Override
	public int getTanks() {
		return 0;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	// Fabric: Added for internal use to reset the content when a Transaction was cancelled
	@Override
	public void setFluidInTank(int tank, FluidStack fluidStack) {
		// noop
	}

	@Override
	public long getTankCapacity(int tank) {
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack fluidStack) {
		return false;
	}

	@Override
	public long fill(FluidStack fluidStack, FluidAction action) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack drainStack, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
