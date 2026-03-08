package com.github.salandora.sophisticatedfabriclib.model.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin_UpdateSprite {
	// lambda inside destroy
	@WrapOperation(
			method = "method_34020",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/client/particle/TerrainParticle;"
			)
	)
	private TerrainParticle sophisticatedFabricLibrary$updateSprite(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state, BlockPos pos, Operation<TerrainParticle> original) {
		TerrainParticle particle = original.call(level, x, y, z, xSpeed, ySpeed, zSpeed, state, pos);
		return particle.sophisticatedFabricLibrary_updateSprite(state, pos);
	}
}
