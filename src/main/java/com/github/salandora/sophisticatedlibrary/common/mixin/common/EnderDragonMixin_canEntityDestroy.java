package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin_canEntityDestroy extends Mob {
	protected EnderDragonMixin_canEntityDestroy(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(
			method = "checkWalls",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"
			)
	)
	public boolean sophisticatedLibrary$checkWalls(boolean original, @Local BlockPos blockPos, @Local BlockState blockState) {
		return original && !blockState.sophisticatedLibrary_canEntityDestroy(this.level(), blockPos, this);
	}
}
