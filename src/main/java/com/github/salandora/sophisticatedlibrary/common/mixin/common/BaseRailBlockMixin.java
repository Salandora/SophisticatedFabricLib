package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.extensions.block.SophisticatedBaseRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BaseRailBlock.class)
public abstract class BaseRailBlockMixin implements SophisticatedBaseRailBlock {
	@Shadow
	public abstract Property<RailShape> getShapeProperty();

	@Override
	public RailShape sophisticatedlibrary_getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {
		return state.getValue(this.getShapeProperty());
	}
}
