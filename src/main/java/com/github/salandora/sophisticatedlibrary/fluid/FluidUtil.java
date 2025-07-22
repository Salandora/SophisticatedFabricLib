package com.github.salandora.sophisticatedlibrary.fluid;

import com.github.salandora.sophisticatedlibrary.transfer.MutableContainerItemContext;
import com.github.salandora.sophisticatedlibrary.transfer.TransferUtil;
import com.mojang.datafixers.util.Function5;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class FluidUtil {
    public static final long BUCKET_VOLUME_IN_MILLIBUCKETS = FluidConstants.BUCKET / 1000;

    public static int toBuckets(long amount) {
        return (int) (amount / BUCKET_VOLUME_IN_MILLIBUCKETS);
    }

    public static boolean isFluidStorage(ItemStack stack) {
        return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;
    }

    public static boolean placeFluid(@Nullable Player player, @NotNull Level level, @NotNull BlockPos pos, Storage<FluidVariant> source, FluidVariant resource, long maxAmount) {
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

        try (Transaction extraction = Transaction.openOuter()) {
			long result = source.extract(resource, maxAmount, extraction);
			if (result == 0) {
				return false;
			}
			extraction.commit();
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
			playFluidSound(player, level, pos, resource, false);
			return true;
		}

		return false;
    }

    public static void playFluidSound(@Nullable Player player, LevelAccessor level, BlockPos pos, FluidVariant resource, boolean fill) {
        SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(resource) : FluidVariantAttributes.getEmptySound(resource);
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    private static boolean moveWithSound(Storage<FluidVariant> from, Storage<FluidVariant> to, long maxAmount, @Nullable Player player, boolean fill, Item handItem, @Nullable Transaction maybeParent) {
        for (StorageView<FluidVariant> view : from) {
            if (view.isResourceBlank()) continue;
            FluidVariant resource = view.getResource();
            long maxExtracted;

            // check how much can be extracted
            try (Transaction extractionTestTransaction = Transaction.openNested(maybeParent)) {
                maxExtracted = view.extract(resource, maxAmount, extractionTestTransaction);
            }

            try (Transaction transferTransaction = Transaction.openNested(maybeParent)) {
                // check how much can be inserted
                long accepted = to.insert(resource, maxExtracted, transferTransaction);

                // extract it, or rollback if the amounts don't match
                if (accepted > 0 && view.extract(resource, accepted, transferTransaction) == accepted) {
                    transferTransaction.commit();

                    SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(resource) : FluidVariantAttributes.getEmptySound(resource);

                    if (resource.isOf(Fluids.WATER)) {
                        if (fill && handItem == Items.GLASS_BOTTLE) sound = SoundEvents.BOTTLE_FILL;
                        if (!fill && handItem == Items.POTION) sound = SoundEvents.BOTTLE_EMPTY;
                    }

                    if (player != null) {
						player.playNotifySound(sound, SoundSource.BLOCKS, 1, 1);
					}

                    return true;
                }
            }
        }

        return false;
    }

	private static FluidActionResult tryTransferFluid(ItemStack container, Storage<FluidVariant> handlerA, long maxAmount, @Nullable Player player, boolean fill, @Nullable Transaction maybeParent) {
		ItemStack containerCopy = container.copyWithCount(1); // do not modify the input

		ContainerItemContext context = new MutableContainerItemContext(containerCopy);
		Storage<FluidVariant> handlerB = context.find(FluidStorage.ITEM);
		if (handlerB == null) {
			return  FluidActionResult.FAILURE;
		}

		try (Transaction nested = Transaction.openNested(maybeParent)) {
			boolean success = moveWithSound(
					fill ? handlerA : handlerB,
					fill ? handlerB : handlerA,
					maxAmount,
					player,
					fill,
					context.getItemVariant().getItem(),
					nested
			);
			if (success) {
				nested.commit();
				return new FluidActionResult(context.getItemVariant(), context.getAmount());
			}
		}

		return FluidActionResult.FAILURE;
	}

	public static FluidActionResult tryFillContainer(ItemStack container, Storage<FluidVariant> fluidSource, long maxAmount, @Nullable Player player, @Nullable Transaction maybeParent) {
		return tryTransferFluid(container, fluidSource, maxAmount, player, true, maybeParent);
	}
	public static FluidActionResult tryEmptyContainer(ItemStack container, Storage<FluidVariant> fluidDestination, long maxAmount, @Nullable Player player, @Nullable Transaction maybeParent) {
		return tryTransferFluid(container, fluidDestination, maxAmount, player, false, maybeParent);
	}

	private static FluidActionResult tryTransferAndStow(ItemStack container, Storage<FluidVariant> fluidSource,
														Storage<ItemVariant> inventory, long maxAmount, @Nullable Player player,
														@Nullable Transaction maybeParent, Function5<ItemStack, Storage<FluidVariant>, Long, Player, Transaction, FluidActionResult> func) {
		if (container.isEmpty()) {
			return FluidActionResult.FAILURE;
		}

		try (Transaction nested = Transaction.openNested(maybeParent)) {
			if (player != null && player.getAbilities().instabuild) {
				FluidActionResult filledReal = func.apply(container, fluidSource, maxAmount, player, nested);
				if (filledReal.isSuccess()) {
					nested.commit();
					return new FluidActionResult(container);
				}
			} else if (container.getCount() == 1) {
				FluidActionResult filledReal = func.apply(container, fluidSource, maxAmount, player, nested);
				if (filledReal.isSuccess()) {
					nested.commit();
					return filledReal;
				}
			} else {
				FluidActionResult filledSimulated = TransferUtil.simulate(simulate -> func.apply(container, fluidSource, maxAmount, player, simulate), nested);
				if (filledSimulated.isSuccess()) {
					// check if we can give the itemStack to the inventory
					long inserted = StorageUtil.simulateInsert(inventory, filledSimulated.getVariant(), filledSimulated.getCount(), nested);
					if (inserted > 0 || player != null) {
						FluidActionResult filledReal;
						try (Transaction insertTransaction = nested.openNested()) {
							filledReal = func.apply(container, fluidSource, maxAmount, player, insertTransaction);
							inserted = StorageUtil.tryInsertStacking(inventory, filledReal.getVariant(), filledReal.getCount(), insertTransaction);
							insertTransaction.commit();
						}

						// give it to the player or drop it at their feet
						if ((filledReal.getCount() - inserted) > 0 && player != null) {
							try (Transaction transferTransaction = nested.openNested()) {
								PlayerInventoryStorage.of(player).offerOrDrop(filledReal.getVariant(), filledReal.getCount() - inserted, transferTransaction);
								transferTransaction.commit();
							}
						}

						nested.commit();

						ItemStack containerCopy = container.copy();
						containerCopy.shrink(1);
						return new FluidActionResult(containerCopy);
					}
				}
			}

			return FluidActionResult.FAILURE;
		}
	}

	public static FluidActionResult tryFillContainerAndStow(ItemStack container, Storage<FluidVariant> fluidSource,
															Storage<ItemVariant> inventory, long maxAmount, @Nullable Player player, boolean doFill) {
		try (Transaction ctx = Transaction.openOuter()) {
			FluidActionResult result = tryTransferAndStow(container, fluidSource, inventory, maxAmount, player, ctx, FluidUtil::tryFillContainer);
			if (result.isSuccess() && doFill) {
				ctx.commit();
			}
			return result;
		}
	}

	public static FluidActionResult tryEmptyContainerAndStow(ItemStack container, Storage<FluidVariant> fluidDestination,
															 Storage<ItemVariant> inventory, long maxAmount, @Nullable Player player, boolean doDrain) {
		try (Transaction ctx = Transaction.openOuter()) {
			FluidActionResult result = tryTransferAndStow(container, fluidDestination, inventory, maxAmount, player, ctx, FluidUtil::tryEmptyContainer);
			if (result.isSuccess() && doDrain) {
				ctx.commit();
			}
			return result;
		}
	}
}
