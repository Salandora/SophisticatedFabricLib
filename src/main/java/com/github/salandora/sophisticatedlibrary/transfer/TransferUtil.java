package com.github.salandora.sophisticatedlibrary.transfer;

import com.github.salandora.sophisticatedlibrary.fluid.api.v1.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface TransferUtil {
	static FluidStack simulateExtractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		return simulateExtractAnyFluid(storage, maxAmount, null);
	}

	static FluidStack simulateExtractAnyFluid(Storage<FluidVariant> storage, long maxAmount, @Nullable TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		FluidVariant extractable = StorageUtil.findExtractableResource(storage, transaction);
		if (extractable != null) {
			long extractableAmount = StorageUtil.simulateExtract(storage, extractable, maxAmount, transaction);
			if (extractableAmount > 0) {
				return new FluidStack(extractable, extractableAmount);
			}
		}

		return FluidStack.EMPTY;
	}

	static <T> T simulate(Function<Transaction, T> func, @Nullable Transaction maybeParent) {
		try (Transaction ctx = Transaction.openNested(maybeParent)) {
			return func.apply(ctx);
		}
	}
}
