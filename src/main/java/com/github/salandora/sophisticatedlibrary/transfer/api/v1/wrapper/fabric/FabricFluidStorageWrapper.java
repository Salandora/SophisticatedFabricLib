package com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric;

import com.github.salandora.sophisticatedlibrary.fluid.api.v1.IFluidHandlerItem;
import com.github.salandora.sophisticatedlibrary.transfer.impl.v1.wrapper.fabric.FabricFluidStorageWrapperImpl;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import javax.annotation.Nullable;

public interface FabricFluidStorageWrapper extends IFluidHandlerItem {
	static FabricFluidStorageWrapper of(Storage<FluidVariant> storage) {
		return FabricFluidStorageWrapperImpl.of(storage, null);
	}
	static FabricFluidStorageWrapper of(Storage<FluidVariant> storage, @Nullable ContainerItemContext container) {
		return FabricFluidStorageWrapperImpl.of(storage, container);
	}
}
