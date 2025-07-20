package com.github.salandora.sophisticatedlibrary.fluid;

import com.github.salandora.sophisticatedlibrary.transfer.TransactionCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BucketPickupHandlerWrapper implements SingleSlotStorage<FluidVariant>, ExtractionOnlyStorage<FluidVariant> {
	private final BucketPickup bucketPickup;
	private final Level level;
	private final BlockPos blockPos;

	public BucketPickupHandlerWrapper(BucketPickup bucketPickup, Level level, BlockPos blockPos) {
		this.bucketPickup = bucketPickup;
		this.level = level;
		this.blockPos = blockPos;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		if (!resource.isBlank() && FluidConstants.BUCKET <= maxAmount) {
			BlockState state = level.getBlockState(blockPos);
			FluidState fluidState = state.getFluidState();
			if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType()) {
				TransactionCallback.onSuccess(transaction, () -> bucketPickup.pickupBlock(null, level, blockPos, state));
				return FluidConstants.BUCKET;
			}
		}
		return 0;
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public FluidVariant getResource() {
		return FluidVariant.of(level.getFluidState(blockPos).getType());
	}

	@Override
	public long getAmount() {
		return !level.getFluidState(blockPos).isEmpty() ? FluidConstants.BUCKET : 0;
	}

	@Override
	public long getCapacity() {
		return FluidConstants.BUCKET;
	}
}
