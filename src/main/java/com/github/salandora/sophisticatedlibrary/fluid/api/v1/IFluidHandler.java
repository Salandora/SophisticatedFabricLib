package com.github.salandora.sophisticatedlibrary.fluid.api.v1;

public interface IFluidHandler {
	int getTanks();

	FluidStack getFluidInTank(int tank);

	void setFluidInTank(int tank, FluidStack fluidStack);

	long getTankCapacity(int tank);

	boolean isFluidValid(int tank, FluidStack fluidStack);

	long fill(FluidStack fluidStack, FluidAction action);

	FluidStack drain(FluidStack drainStack, FluidAction action);

	FluidStack drain(long maxDrain, FluidAction action);

	enum FluidAction {
		EXECUTE,
		SIMULATE;

		public boolean execute() {
			return this == EXECUTE;
		}

		public boolean simulate() {
			return this == SIMULATE;
		}
	}
}
