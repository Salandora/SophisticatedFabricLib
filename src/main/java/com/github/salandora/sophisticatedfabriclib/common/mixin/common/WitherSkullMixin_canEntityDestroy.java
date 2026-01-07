package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherSkull.class)
public class WitherSkullMixin_canEntityDestroy {
	@ModifyExpressionValue(
			method = "getBlockExplosionResistance",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/WitherSkull;isDangerous()Z"
			)
	)
	public boolean sophisticatedLibrary$getBlockExplosionResistance(boolean original, @Local(argsOnly = true) BlockGetter blockGetter, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) BlockState blockState) {
		return original && blockState.sophisticatedLibrary_canEntityDestroy(blockGetter, blockPos, (Entity) (Object) this);
	}
}
