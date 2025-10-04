package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin_LandingEffect {
	@WrapWithCondition(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
			)
	)
	public boolean sophisticatedLibrary$addLandingEffects(ServerLevel level,
														  ParticleOptions type,
														  double posX, double posY, double posZ,
														  int particleCount,
														  double xOffset, double yOffset, double zOffset,
														  double speed,
														  @Local(argsOnly = true) BlockState state,
														  @Local(argsOnly = true) BlockPos pos) {
		return !state.sophisticatedLibrary_addLandingEffects(level, pos, state, (LivingEntity) (Object) this, particleCount);
	}
}
