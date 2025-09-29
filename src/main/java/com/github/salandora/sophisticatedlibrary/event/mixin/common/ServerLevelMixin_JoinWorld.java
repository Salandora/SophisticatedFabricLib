package com.github.salandora.sophisticatedlibrary.event.mixin.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.github.salandora.sophisticatedlibrary.event.api.common.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin_JoinWorld {
	@Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
	public void sophisticateCore$addEntityEvent(ServerPlayer serverPlayer, CallbackInfo ci) {
		if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(serverPlayer, (ServerLevel) (Object) this, false))
			ci.cancel();
	}
}
