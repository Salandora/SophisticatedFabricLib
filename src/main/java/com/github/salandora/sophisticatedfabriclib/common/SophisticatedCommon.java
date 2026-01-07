package com.github.salandora.sophisticatedfabriclib.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.block.entity.SophisticatedBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.TickTask;

public class SophisticatedCommon implements ModInitializer {
    @Override
    public void onInitialize() {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof SophisticatedBlockEntity sbe) {
				// Force the onLoad to the next tick or else the game will indefinitely hang as it can't get the chunk
				world.getServer().tell(new TickTask(world.getServer().getTickCount(), sbe::sophisticatedLibrary_onLoad));
			}
		});
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof SophisticatedBlockEntity sbe) {
				sbe.sophisticatedLibrary_invalidateCaps();
			}
		});
		ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) ->
				chunk.getBlockEntities().forEach((pos, blockEntity) -> {
					if (blockEntity instanceof SophisticatedBlockEntity sbe) {
						sbe.sophisticatedLibrary_onChunkUnloaded();
					}
				}));
    }
}
