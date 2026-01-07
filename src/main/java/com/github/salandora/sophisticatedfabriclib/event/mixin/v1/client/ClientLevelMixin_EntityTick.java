package com.github.salandora.sophisticatedfabriclib.event.mixin.v1.client;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.EntityTickEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin_EntityTick {
	@WrapOperation(
			method = "tickNonPassenger",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;tick()V"
			)
	)
	public void sophisticatedLibrary$entityTick(Entity instance, Operation<Void> original) {
		original.call(instance);
		EntityTickEvents.POST.invoker().onLivingEntityTick(instance);
	}
}
