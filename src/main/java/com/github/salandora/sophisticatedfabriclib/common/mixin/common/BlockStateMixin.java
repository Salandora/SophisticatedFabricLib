package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.block.SophisticatedBlockState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements SophisticatedBlockState {
}
