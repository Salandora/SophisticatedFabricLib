package com.github.salandora.sophisticatedlibrary.common.mixin;

import com.github.salandora.sophisticatedlibrary.common.extensions.block.SophisticatedBlockStateExtension;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements SophisticatedBlockStateExtension {
}
