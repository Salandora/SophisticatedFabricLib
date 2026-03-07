package com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.IEnergyStorage;
import com.github.salandora.sophisticatedfabriclib.energy.impl.v1.wrapper.teamreborn.EnergyStorageWrapperImpl;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

public interface EnergyStorageWrapper extends IEnergyStorage {
	static EnergyStorageWrapper of(EnergyStorage storage) {
		return of(storage, null);
	}
	static EnergyStorageWrapper of(EnergyStorage storage, @Nullable ContainerItemContext container) {
		return EnergyStorageWrapperImpl.of(storage, container);
	}
}
