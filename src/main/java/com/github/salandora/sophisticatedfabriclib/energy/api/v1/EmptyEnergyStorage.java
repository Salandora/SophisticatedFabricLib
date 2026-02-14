package com.github.salandora.sophisticatedfabriclib.energy.api.v1;

public class EmptyEnergyStorage implements IEnergyStorage {
	public static final IEnergyStorage INSTANCE = new EmptyEnergyStorage();

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public void setEnergyStored(int stored) {
	}

	@Override
	public int getEnergyStored() {
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return 0;
	}
}
