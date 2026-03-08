package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorMixin_ExplosionResistance {
	@ModifyExpressionValue(
			method = "getBlockExplosionResistance",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getExplosionResistance()F"
			)
	)
	private float sophisticatedBackpacks$getExplosionResistance(float original,
																@Local(argsOnly = true) Explosion explosion,
																@Local(argsOnly = true) BlockGetter reader,
																@Local(argsOnly = true) BlockState state,
																@Local(argsOnly = true) BlockPos pos) {
		float resistance = state.sophisticatedFabricLibrary_getExplosionResistance(reader, pos, explosion);
		return Float.isNaN(resistance) ? original : resistance;
	}
}
