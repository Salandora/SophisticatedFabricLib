package com.github.salandora.sophisticatedlibrary.fluid.api.v1;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleFluidContent implements DataComponentHolder {
	public static final SimpleFluidContent EMPTY = new SimpleFluidContent(FluidStack.EMPTY);

	public static final Codec<SimpleFluidContent> CODEC = FluidStack.OPTIONAL_CODEC
			.xmap(SimpleFluidContent::new, content -> content.fluidStack);
	public static final StreamCodec<RegistryFriendlyByteBuf, SimpleFluidContent> STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC
			.map(SimpleFluidContent::new, content -> content.fluidStack);

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

	public FluidStack copy() {
		return this.fluidStack.copy();
	}

	public long getAmount() {
		return this.fluidStack.getAmount();
	}
}
