package com.github.salandora.sophisticatedfabriclib.event.mixin.v1.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.EntityTickEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class Entity_EntityTick {
	@WrapOperation(
			method = "rideTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;tick()V"
			)
	)
	public void sophisticatedFabricLibrary$entityTick(Entity instance, Operation<Void> original) {
		original.call(instance);
		EntityTickEvents.POST.invoker().onLivingEntityTick(instance);
	}
}
