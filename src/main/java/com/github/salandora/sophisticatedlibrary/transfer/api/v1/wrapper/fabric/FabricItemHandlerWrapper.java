package com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric;

import com.github.salandora.sophisticatedlibrary.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedlibrary.transfer.impl.v1.wrapper.fabric.FabricItemHandlerWrapperImpl;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;

public interface FabricItemHandlerWrapper extends SlottedStorage<ItemVariant> {
	static FabricItemHandlerWrapper of(IItemHandler handler) {
		return FabricItemHandlerWrapperImpl.of(handler);
	}

	IItemHandler getHandler();
}
