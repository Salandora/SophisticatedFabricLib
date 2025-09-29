package com.github.salandora.sophisticatedlibrary.common.extensions.block;

import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface SophisticatedBlockState {
	private BlockState self() {
		return (BlockState) this;
	}

	default BlockState sophisticatedLibrary_getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return self().getBlock().sophisticatedLibrary_getToolModifiedState(self(), context, itemAbility, simulate);
	}

	default boolean sophisticatedLibrary_addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return self().getBlock().sophisticatedLibrary_addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
	}

	default boolean sophisticatedLibrary_addRunningEffects(Level level, BlockPos pos, Entity entity) {
		return self().getBlock().sophisticatedLibrary_addRunningEffects(self(), level, pos, entity);
	}

	@Environment(EnvType.CLIENT)
	default boolean sophisticatedLibrary_addHitEffects(Level level, HitResult target, ParticleEngine manager) {
		return self().getBlock().sophisticatedLibrary_addHitEffects(self(), level, target, manager);
	}

	@Environment(EnvType.CLIENT)
	default boolean sophisticatedLibrary_addDestroyEffects(Level level, BlockPos pos, ParticleEngine manager) {
		return self().getBlock().sophisticatedLibrary_addDestroyEffects(self(), level, pos, manager);
	}
}
