package com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric;

import com.github.salandora.sophisticatedlibrary.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedlibrary.transfer.impl.v1.wrapper.fabric.FabricFluidHandlerWrapperImpl;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface FabricFluidHandlerWrapper extends Storage<FluidVariant> {
	static FabricFluidHandlerWrapper of(IFluidHandler handler) {
		return FabricFluidHandlerWrapperImpl.of(handler);
	}

	IFluidHandler getHandler();
}
