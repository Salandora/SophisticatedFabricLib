package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin_RunningEffect {
	@Shadow
	private Level level;

	@Inject(
			method = "spawnSprintParticle",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"),
			cancellable = true
	)
	private void sophisticatedFabricLibrary$addRunningEffects(CallbackInfo ci, @Local BlockPos blockPos, @Local BlockState blockState) {
		if (blockState.sophisticatedFabricLibrary_addRunningEffects(level, blockPos, (Entity) (Object) this)) {
			ci.cancel();
		}
	}
}
