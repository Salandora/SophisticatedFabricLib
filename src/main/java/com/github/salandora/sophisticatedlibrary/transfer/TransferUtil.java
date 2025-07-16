package com.github.salandora.sophisticatedlibrary.transfer;

import com.github.salandora.sophisticatedlibrary.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import javax.annotation.Nullable;

public interface TransferUtil {
	static FluidStack simulateExtractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		return simulateExtractAnyFluid(storage, maxAmount, null);
	}

	static FluidStack simulateExtractAnyFluid(Storage<FluidVariant> storage, long maxAmount, @Nullable TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		FluidVariant extractable = StorageUtil.findExtractableResource(storage, transaction);
		if (extractable != null && !extractable.isBlank()) {
			return new FluidStack(extractable, StorageUtil.simulateExtract(storage, extractable, maxAmount, transaction));
		}

		return FluidStack.EMPTY;
	}
}
