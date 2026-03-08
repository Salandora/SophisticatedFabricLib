package com.github.salandora.sophisticatedfabriclib.fluid.api.v1;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.TransferUtil;
import com.github.salandora.sophisticatedfabriclib.util.Capabilities;
import com.mojang.datafixers.util.Function5;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class FluidUtil {
    public static final long BUCKET_VOLUME_IN_MILLIBUCKETS = FluidConstants.BUCKET / 1000;

    public static int toBuckets(long amount) {
        return (int) (amount / BUCKET_VOLUME_IN_MILLIBUCKETS);
    }

    public static boolean isFluidStorage(ItemStack stack) {
		return ContainerItemContext.withConstant(stack).find(Capabilities.FluidHandler.ITEM) != null;
    }

	public static Optional<IFluidHandlerItem> getFluidHandler(ItemStack stack) {
		return Optional.ofNullable(stack.sophisticatedFabricLibrary_getCapability(Capabilities.FluidHandler.ITEM));
	}

	public static Optional<IFluidHandler> getFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
		return Optional.ofNullable(Capabilities.FluidHandler.SIDED.find(level, pos, side));
	}

	public static boolean tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, IFluidHandler source, FluidStack resource) {
		if (!level.isLoaded(pos)) {
			return false;
		}

		Fluid fluid = resource.getFluid();
		if (fluid == Fluids.EMPTY) {
			return false;
		}

		// check that we can place the fluid at the destination
		BlockState state = level.getBlockState(pos);
		boolean waterlog = state.hasProperty(WATERLOGGED);
		if (!waterlog && !state.canBeReplaced()) {
			return false;
		}
		if (waterlog && fluid != Fluids.WATER) {
			return false;
		}

		FluidState fluidState = state.getFluidState();
		if (!fluidState.isEmpty() && fluidState.getType() != fluid) {
			return false;
		}

		FluidStack drained = source.drain(resource, IFluidHandler.FluidAction.EXECUTE);
		if (drained.isEmpty()) {
			return false;
		}

		if (level.dimensionType().ultraWarm() && fluid.defaultFluidState().is(FluidTags.WATER)) {
			level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
			for (int i = 0; i < 8; ++i) {
				level.addParticle(ParticleTypes.LARGE_SMOKE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
			}
			return true;
		}

		if (waterlog) {
			level.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
			level.scheduleTick(pos, Fluids.WATER, 1);
			return true;
		}

		if (!level.isClientSide && state.canBeReplaced(fluid) && !state.liquid()) {
			level.destroyBlock(pos, true);
		}

		if (level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 3) || fluidState.isSource()) {
			level.playSound(player, pos, FluidVariantAttributes.getEmptySound(resource.getVariant()), SoundSource.BLOCKS, 1.0f, 1.0f);
			level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
			return true;
		}

		return false;
	}


	private static FluidStack tryTransferFluid(IFluidHandler to, IFluidHandler from, FluidStack extractable, boolean doTransfer) {
		long accepted = to.fill(extractable, IFluidHandler.FluidAction.SIMULATE);
		if (accepted > 0) {
			extractable.setAmount(accepted);
			if (doTransfer) {
				FluidStack extracted = from.drain(extractable, IFluidHandler.FluidAction.EXECUTE);
				if (!extracted.isEmpty()) {
					extracted.setAmount(to.fill(extracted, IFluidHandler.FluidAction.EXECUTE));
					return extracted;
				}
			}
			return extractable;
		}

		return FluidStack.EMPTY;
	}
    public static FluidStack tryFluidTransfer(IFluidHandler to, IFluidHandler from, long maxAmount, boolean doTransfer) {
		FluidStack extractable = from.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
		if (!extractable.isEmpty()) {
			return tryTransferFluid(to, from, extractable, doTransfer);
		}
		return FluidStack.EMPTY;
    }
	public static FluidStack tryFluidTransfer(IFluidHandler to, IFluidHandler from, FluidStack resource, boolean doTransfer) {
		FluidStack extractable = from.drain(resource, IFluidHandler.FluidAction.SIMULATE);
		if (!extractable.isEmpty() && FluidStack.isSameFluidSameComponents(resource, extractable)) {
			return tryTransferFluid(to, from, extractable, doTransfer);
		}
		return FluidStack.EMPTY;
	}


	private static FluidActionResult tryTransferFluid(ItemStack container, IFluidHandler handlerA, long maxAmount, @Nullable Player player, boolean fill, boolean doTransfer) {
		ItemStack containerCopy = container.copyWithCount(1); // do not modify the input
		return getFluidHandler(containerCopy)
				.map(handlerB -> {
					IFluidHandler to = fill ? handlerB : handlerA;
					IFluidHandler from = fill ? handlerA : handlerB;

					FluidStack simulated = tryFluidTransfer(to, from, maxAmount,false);
					if (!simulated.isEmpty()) {
						if (doTransfer) {
							tryFluidTransfer(to, from, maxAmount,true);
							if (player != null) {
								SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(simulated.getVariant()) : FluidVariantAttributes.getEmptySound(simulated.getVariant());
								player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.BLOCKS, 1, 1);
							}
						} else {
							// Act on the handler so we get the correct container item back
							if (fill) {
								handlerB.fill(simulated, IFluidHandler.FluidAction.EXECUTE);
							} else {
								handlerB.drain(simulated, IFluidHandler.FluidAction.EXECUTE);
							}
						}
						return new FluidActionResult(handlerB.getContainer());
					}
					return FluidActionResult.FAILURE;
				})
				.orElse(FluidActionResult.FAILURE);
	}
	public static FluidActionResult tryFillContainer(ItemStack container, IFluidHandler source, long maxAmount, @Nullable Player player, boolean doFill) {
		return tryTransferFluid(container, source, maxAmount, player, true, doFill);
	}
	public static FluidActionResult tryEmptyContainer(ItemStack container, IFluidHandler destination, long maxAmount, @Nullable Player player, boolean doDrain) {
		return tryTransferFluid(container, destination, maxAmount, player, false, doDrain);
	}


	private static FluidActionResult tryTransferAndStow(ItemStack container, IFluidHandler fluidSource,
														IItemHandler inventory, long maxAmount, @Nullable Player player,
														boolean doTransfer, Function5<ItemStack, IFluidHandler, Long, Player, Boolean, FluidActionResult> func) {
		if (container.isEmpty()) {
			return FluidActionResult.FAILURE;
		}

		if (player != null && player.getAbilities().instabuild) {
			FluidActionResult filledReal = func.apply(container, fluidSource, maxAmount, player, doTransfer);
			if (filledReal.isSuccess()) {
				return new FluidActionResult(container);
			}
		} else if (container.getCount() == 1) {
			FluidActionResult filledReal = func.apply(container, fluidSource, maxAmount, player, doTransfer);
			if (filledReal.isSuccess()) {
				return filledReal;
			}
		} else {
			FluidActionResult filledSimulated = func.apply(container, fluidSource, maxAmount, player, false);
			if (filledSimulated.isSuccess()) {
				// check if we can give the itemStack to the inventory
				ItemStack remainder = TransferUtil.insertItemStacked(inventory, filledSimulated.getResult(), true);
				if (remainder.isEmpty() || player != null) {
					FluidActionResult filledReal = func.apply(container, fluidSource, maxAmount, player, doTransfer);
					remainder = TransferUtil.insertItemStacked(inventory, filledReal.getResult(), !doTransfer);

					// give it to the player or drop it at their feet
					if (!remainder.isEmpty() && player != null && doTransfer) {
						TransferUtil.giveOrDropToPlayer(player, filledReal.getResult());
					}

					ItemStack containerCopy = container.copy();
					containerCopy.shrink(1);
					return new FluidActionResult(containerCopy);
				}
			}
		}

		return FluidActionResult.FAILURE;
	}
	public static FluidActionResult tryFillContainerAndStow(ItemStack container, IFluidHandler fluidSource,
															IItemHandler inventory, long maxAmount, @Nullable Player player, boolean doFill) {
		return tryTransferAndStow(container, fluidSource, inventory, maxAmount, player, doFill, FluidUtil::tryFillContainer);
	}
	public static FluidActionResult tryEmptyContainerAndStow(ItemStack container, IFluidHandler fluidDestination,
															 IItemHandler inventory, long maxAmount, @Nullable Player player, boolean doDrain) {
		return tryTransferAndStow(container, fluidDestination, inventory, maxAmount, player, doDrain, FluidUtil::tryEmptyContainer);
	}
}
