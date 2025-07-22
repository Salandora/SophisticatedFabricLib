package com.github.salandora.sophisticatedlibrary.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class TestFluidStorage extends SingleVariantStorage<FluidVariant> {
	private final long capacity;

	public TestFluidStorage(long capacity) {
		this.capacity = capacity;
	}

	@Override
	protected FluidVariant getBlankVariant() {
		return FluidVariant.blank();
	}

	@Override
	protected long getCapacity(FluidVariant variant) {
		return capacity;
	}

	public void insertTestFluid(FluidVariant fluid, long amount) {
		try (Transaction tx = Transaction.openOuter()) {
			this.insert(fluid, amount, tx);
			tx.commit();
		}
	}
}