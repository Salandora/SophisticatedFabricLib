package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.EntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin_JoinWorld {
	@Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
	public void sophisticateLibrary$addEntityEvent(ServerPlayer serverPlayer, CallbackInfo ci) {
		if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(serverPlayer, (ServerLevel) (Object) this, false))
			ci.cancel();
	}
}
