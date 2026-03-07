package com.github.salandora.sophisticatedfabriclib.energy.api.v1;

import net.minecraft.world.item.ItemStack;

public interface IEnergyStorage {
	int receiveEnergy(int maxReceive, boolean simulate);

	int extractEnergy(int maxExtract, boolean simulate);

	void setEnergyStored(int stored);
	
	int getEnergyStored();

	int getMaxEnergyStored();

	boolean canExtract();

	boolean canReceive();

	default ItemStack getContainer() { return ItemStack.EMPTY; }
}
