package com.github.salandora.sophisticatedlibrary.fluid;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;

public class SimpleFluidContent implements DataComponentHolder {
	public static final SimpleFluidContent EMPTY = new SimpleFluidContent(FluidStack.EMPTY);

	public static SimpleFluidContent copyOf(FluidStack fluidStack) {
		return fluidStack.isResourceBlank() ? EMPTY : new SimpleFluidContent(fluidStack.copy());
	}

	private final FluidStack fluidStack;

	public SimpleFluidContent(FluidStack fluidStack) {
		this.fluidStack = fluidStack;
	}

	@Override
	public DataComponentMap getComponents() {
		return fluidStack.getVariant().getComponentMap();
	}
}
