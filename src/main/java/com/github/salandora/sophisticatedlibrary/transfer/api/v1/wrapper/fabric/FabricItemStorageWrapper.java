package com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric;

import com.github.salandora.sophisticatedlibrary.transfer.api.v1.IItemHandlerModifiable;
import com.github.salandora.sophisticatedlibrary.transfer.impl.v1.wrapper.fabric.FabricItemStorageWrapperImpl;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface FabricItemStorageWrapper extends IItemHandlerModifiable {
	static FabricItemStorageWrapper of(Storage<ItemVariant> storage) {
		return FabricItemStorageWrapperImpl.of(storage);
	}
}
