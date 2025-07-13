package com.github.salandora.sophisticatedlibrary.common.mixin;

import com.github.salandora.sophisticatedlibrary.common.extensions.block.SophisticatedBlockExtension;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements SophisticatedBlockExtension {
}
