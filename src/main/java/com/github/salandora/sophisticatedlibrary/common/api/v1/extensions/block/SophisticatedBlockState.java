package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.block;

import com.github.salandora.sophisticatedlibrary.common.api.v1.ItemAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface SophisticatedBlockState {
	private BlockState self() {
		return (BlockState) this;
	}

	default @Nullable BlockState sophisticatedLibrary_getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return self().getBlock().sophisticatedLibrary_getToolModifiedState(self(), context, itemAbility, simulate);
	}

	default boolean sophisticatedLibrary_addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return self().getBlock().sophisticatedLibrary_addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
	}

	default boolean sophisticatedLibrary_addRunningEffects(Level level, BlockPos pos, Entity entity) {
		return self().getBlock().sophisticatedLibrary_addRunningEffects(self(), level, pos, entity);
	}

	default float sophisticatedLibrary_getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
		return self().getBlock().sophisticatedLibrary_getExplosionResistance(self(), level, pos, explosion);
	}

	default boolean sophisticatedLibrary_canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
		return self().getBlock().sophisticatedLibrary_canEntityDestroy(self(), level, pos, entity);
	}
}
