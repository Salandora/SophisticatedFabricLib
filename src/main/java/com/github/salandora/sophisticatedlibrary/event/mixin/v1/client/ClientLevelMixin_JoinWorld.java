package com.github.salandora.sophisticatedlibrary.event.mixin.v1.client;

import com.github.salandora.sophisticatedlibrary.event.api.common.EntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin_JoinWorld {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void sophisticatedLibrary$addEntityEvent(Entity entity, CallbackInfo ci) {
        if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, (ClientLevel) (Object) this, false))
            ci.cancel();
    }
}
