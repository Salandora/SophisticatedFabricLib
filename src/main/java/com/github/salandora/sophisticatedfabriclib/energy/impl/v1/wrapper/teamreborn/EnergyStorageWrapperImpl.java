package com.github.salandora.sophisticatedfabriclib.energy.impl.v1.wrapper.teamreborn;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn.EnergyStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;


public class EnergyStorageWrapperImpl implements EnergyStorageWrapper {
	public static EnergyStorageWrapper of(EnergyStorage storage, @Nullable ContainerItemContext context) {
		return new EnergyStorageWrapperImpl(storage, context);
	}

	private final EnergyStorage storage;
	@Nullable
	private final ContainerItemContext container;

	public EnergyStorageWrapperImpl(EnergyStorage storage, @Nullable ContainerItemContext container) {
		this.storage = storage;
		this.container = container;
	}

	@Override
	@Nullable
	public ItemStack getContainer() {
		return this.container.getItemVariant().toStack((int) this.container.getAmount());
	}

	@Override
	public void setEnergyStored(int stored) {
		// noop
	}

	@Override
	public int getEnergyStored() {
		return (int) storage.getAmount();
	}

	@Override
	public int getMaxEnergyStored() {
		return (int) storage.getCapacity();
	}

	@Override
	public boolean canExtract() {
		return storage.supportsExtraction();
	}

	@Override
	public boolean canReceive() {
		return storage.supportsInsertion();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (maxReceive == 0) {
			return 0;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long inserted = storage.insert(maxReceive, ctx);
			if (!simulate) {
				ctx.commit();
			}

			return (int) inserted;
		}
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (maxExtract == 0) {
			return 0;
		}

		try (Transaction ctx = Transaction.openOuter()) {
			long extracted = storage.extract(maxExtract, ctx);
			if (!simulate) {
				ctx.commit();
			}

			return (int) extracted;
		}
	}
}
