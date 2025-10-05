package com.github.salandora.sophisticatedlibrary.common.api.v1.client;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class ClientExtensionManager {
	public static final BlockApiLookup<IClientBlockExtensions, Void> BLOCK_EXTENSIONS = BlockApiLookup.get(ResourceLocation.fromNamespaceAndPath(SophisticatedLibrary.MOD_ID, "block_extensions"), IClientBlockExtensions.class, Void.class);

	public static void registerBlock(IClientBlockExtensions extension, Block block) {
		BLOCK_EXTENSIONS.registerForBlocks((world, pos, state, blockEntity, direction) -> extension, block);
	}
}
