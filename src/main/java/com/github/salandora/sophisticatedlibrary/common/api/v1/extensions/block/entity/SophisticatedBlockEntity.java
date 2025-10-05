package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.block.entity;

import net.fabricmc.fabric.impl.lookup.block.ServerWorldCache;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface SophisticatedBlockEntity {
	private BlockEntity self() {
		return (BlockEntity) this;
	}

	default void sophisticatedLibrary_invalidateCapabilities() {
		BlockEntity be = self();
		if (!(be.getLevel() instanceof ServerWorldCache serverWorldCache)) {
			return;
		}

		serverWorldCache.fabric_invalidateCache(be.getBlockPos());
	}

	default void sophisticatedLibrary_onLoad() {
	}

	default void sophisticatedLibrary_onChunkUnloaded() {
	}
}
