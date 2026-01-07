package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.EntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin_JoinWorld<T extends EntityAccess> {
	@Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
	public void sophisticatedLibrary$addEntityEvent(T entityAccess, boolean loadedFromDisk, CallbackInfoReturnable<Boolean> cir) {
		if (entityAccess instanceof Entity entity && EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, entity.level(), loadedFromDisk))
			cir.setReturnValue(false);
	}
}
