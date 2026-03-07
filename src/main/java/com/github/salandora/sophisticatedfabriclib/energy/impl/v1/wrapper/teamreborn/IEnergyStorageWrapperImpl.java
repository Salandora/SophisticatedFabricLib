package com.github.salandora.sophisticatedfabriclib.energy.impl.v1.wrapper.teamreborn;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.IEnergyStorage;
import com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn.IEnergyStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class IEnergyStorageWrapperImpl extends SnapshotParticipant<Integer> implements IEnergyStorageWrapper {
	public static IEnergyStorageWrapper of(IEnergyStorage storage) {
		if (storage == null) {
			return null;
		}

		return new IEnergyStorageWrapperImpl(storage);
	}

	private final IEnergyStorage storage;

	private IEnergyStorageWrapperImpl(IEnergyStorage storage) {
		this.storage = storage;
	}

	public IEnergyStorage getEnergyStorage() {
		return this.storage;
	}


	@Override
	public boolean supportsInsertion() {
		return storage.canReceive();
	}

	@Override
	public long insert(long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		/*ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});*/

		return storage.receiveEnergy((int) maxAmount, false);
	}

	@Override
	public boolean supportsExtraction() {
		return storage.canExtract();
	}

	@Override
	public long extract(long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		/*ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});*/

		return storage.extractEnergy((int) maxAmount, false);
	}

	@Override
	public long getAmount() {
		return this.storage.getEnergyStored();
	}

	@Override
	public long getCapacity() {
		return this.storage.getMaxEnergyStored();
	}

	@Override
	protected Integer createSnapshot() {
		return this.storage.getEnergyStored();
	}

	@Override
	protected void readSnapshot(Integer snapshot) {
		this.storage.setEnergyStored(snapshot);
	}
}
