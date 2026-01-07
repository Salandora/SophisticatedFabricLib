package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.LivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin_tick {
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;tick()V"
			)
	)
	private void sophisticatedLibrary$tick(CallbackInfo ci) {
		LivingEntityEvents.TICK.invoker().onLivingEntityTick((LivingEntity) (Object) this);
	}
}
