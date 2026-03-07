package com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.IEnergyStorage;
import com.github.salandora.sophisticatedfabriclib.energy.impl.v1.wrapper.teamreborn.IEnergyStorageWrapperImpl;
import team.reborn.energy.api.EnergyStorage;

public interface IEnergyStorageWrapper extends EnergyStorage {
	static IEnergyStorageWrapper of(IEnergyStorage storage) {
		return IEnergyStorageWrapperImpl.of(storage);
	}

	IEnergyStorage getEnergyStorage();
}
