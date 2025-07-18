package com.github.salandora.sophisticatedlibrary.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Collections;
import java.util.Iterator;

public class EmptyFluidHandler implements IFluidHandler {
	public static EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return Collections.emptyIterator();
	}
}
