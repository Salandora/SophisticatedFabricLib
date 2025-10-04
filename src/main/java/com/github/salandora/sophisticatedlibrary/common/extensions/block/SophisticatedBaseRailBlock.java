package com.github.salandora.sophisticatedlibrary.common.extensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.Nullable;

public interface SophisticatedBaseRailBlock {
	RailShape sophisticatedlibrary_getRailDirection(BlockState var1, BlockGetter var2, BlockPos var3, @Nullable AbstractMinecart var4);
}
