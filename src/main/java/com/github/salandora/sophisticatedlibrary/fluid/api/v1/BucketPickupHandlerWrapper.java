package com.github.salandora.sophisticatedlibrary.fluid.api.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class BucketPickupHandlerWrapper implements IFluidHandler {
	private final Player player;
	private final BucketPickup bucketPickup;
	private final Level level;
	private final BlockPos blockPos;

	public BucketPickupHandlerWrapper(@Nullable Player player, BucketPickup bucketPickup, Level level, BlockPos blockPos) {
		this.player = player;
		this.bucketPickup = bucketPickup;
		this.level = level;
		this.blockPos = blockPos;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		FluidState fluidState = level.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			return new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
		}
		return FluidStack.EMPTY;
	}

	@Override
	public void setFluidInTank(int tank, FluidStack fluidStack) {
		// noop
	}

	@Override
	public long getTankCapacity(int tank) {
		return FluidType.BUCKET_VOLUME;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack fluidStack) {
		return true;
	}

	@Override
	public long fill(FluidStack fluidStack, FluidAction action) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || resource.getAmount() < FluidType.BUCKET_VOLUME) {
			return FluidStack.EMPTY;
		}

		BlockState state = level.getBlockState(blockPos);
		FluidState fluidState = state.getFluidState();
		if (action.simulate()) {
			FluidStack extracted = new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
			if (FluidStack.isSameFluidSameComponents(resource, extracted)) {
				return extracted;
			}
		} else if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType()) {
			ItemStack pickup = bucketPickup.pickupBlock(null, level, blockPos, state);
			if (!pickup.isEmpty() && pickup.getItem() instanceof BucketItem bucket) {
				FluidStack extracted = new FluidStack(bucket.content, FluidType.BUCKET_VOLUME);
				return FluidStack.isSameFluidSameComponents(resource, extracted) ? extracted : FluidStack.EMPTY;
			}
		}

		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(long maxDrain, FluidAction action) {
		if (FluidType.BUCKET_VOLUME <= maxDrain) {
			FluidState fluidState = level.getFluidState(blockPos);
			if (!fluidState.isEmpty()) {
				if (action.simulate()) {
					return new FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME);
				}

				ItemStack itemStack = bucketPickup.pickupBlock(player, level, blockPos, level.getBlockState(blockPos));
				if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof BucketItem bucket) {
					return new FluidStack(bucket.content, FluidType.BUCKET_VOLUME);
				}
			}
		}
		return FluidStack.EMPTY;
	}
}
