package com.github.salandora.sophisticatedlibrary.model.api.v1.extensions.client.particle;

import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface SophisticatedTerrainParticle {
	default TerrainParticle sophisticatedLibrary_updateSprite(BlockState state, @Nullable BlockPos pos) {
		throw new RuntimeException("Should have been overridden");
	}
}
