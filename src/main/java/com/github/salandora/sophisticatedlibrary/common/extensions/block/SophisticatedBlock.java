package com.github.salandora.sophisticatedlibrary.common.extensions.block;

import com.github.salandora.sophisticatedlibrary.common.ItemAbilities;
import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import com.github.salandora.sophisticatedlibrary.common.mixin.common.accessors.AxeItemAccessor;
import com.github.salandora.sophisticatedlibrary.common.mixin.common.accessors.ShovelItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Optional;

public interface SophisticatedBlock {
	@Nullable
	default BlockState sophisticatedLibrary_getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack stack = context.getItemInHand();
		if (!stack.sophisticatedLibrary_canPerformAction(itemAbility)) {
			return null;
		}
		if (ItemAbilities.AXE_STRIP == itemAbility) {
			Block block = AxeItemAccessor.getStrippedBlocks().get(state.getBlock());
			return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
		} else if (ItemAbilities.AXE_SCRAPE == itemAbility) {
			return WeatheringCopper.getPrevious(state).orElse(null);
		} else if (ItemAbilities.AXE_WAX_OFF == itemAbility) {
			return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state)).orElse(null);
		} else if (ItemAbilities.SHOVEL_FLATTEN == itemAbility) {
			return ShovelItemAccessor.getPathStates().get(state.getBlock());
		} else if (ItemAbilities.HOE_TILL == itemAbility) {
			// Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
			Block block = state.getBlock();
			if (block == Blocks.ROOTED_DIRT) {
				if (!simulate && !context.getLevel().isClientSide) {
					Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
				}
				return Blocks.DIRT.defaultBlockState();
			} else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
					context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
				return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
			}
		} else if (ItemAbilities.SHEARS_TRIM == itemAbility) {
			if (state.getBlock() instanceof GrowingPlantHeadBlock growingPlant && !growingPlant.isMaxAge(state)) {
				if (!simulate)
					context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
				return growingPlant.getMaxAgeState(state);
			}
		} else if (ItemAbilities.SHOVEL_DOUSE == itemAbility) {
			if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
				if (!simulate) {
					CampfireBlock.dowse(context.getPlayer(), context.getLevel(), context.getClickedPos(), state);
				}
				return state.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
			}
		}

		return null;
	}

	default boolean sophisticatedLibrary_addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return false;
	}

	default boolean sophisticatedLibrary_addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default boolean sophisticatedLibrary_addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default boolean sophisticatedLibrary_addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
		return !state.shouldSpawnTerrainParticles();
	}
}
