package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.block;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbility;
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

	default @Nullable BlockState sophisticatedFabricLibrary_getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return self().getBlock().sophisticatedFabricLibrary_getToolModifiedState(self(), context, itemAbility, simulate);
	}

	default boolean sophisticatedFabricLibrary_addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return self().getBlock().sophisticatedFabricLibrary_addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
	}

	default boolean sophisticatedFabricLibrary_addRunningEffects(Level level, BlockPos pos, Entity entity) {
		return self().getBlock().sophisticatedFabricLibrary_addRunningEffects(self(), level, pos, entity);
	}

	default float sophisticatedFabricLibrary_getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
		return self().getBlock().sophisticatedFabricLibrary_getExplosionResistance(self(), level, pos, explosion);
	}

	default boolean sophisticatedFabricLibrary_canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
		return self().getBlock().sophisticatedFabricLibrary_canEntityDestroy(self(), level, pos, entity);
	}
}
