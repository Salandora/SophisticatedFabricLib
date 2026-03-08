package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin_canEntityDestroy extends Entity {
	public WitherBossMixin_canEntityDestroy(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(
			method = "customServerAiStep",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"
			)
	)
	public boolean sophisticatedFabricLibrary$customServerAiStep(boolean original, @Local BlockPos blockPos, @Local BlockState blockState) {
		return original && !blockState.sophisticatedFabricLibrary_canEntityDestroy(this.level(), blockPos, this);
	}
}
