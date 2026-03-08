package com.github.salandora.sophisticatedfabriclib.event.mixin.v1.client;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.EntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin_JoinWorld {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void sophisticatedFabricLibrary$addEntityEvent(Entity entity, CallbackInfo ci) {
        if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, (ClientLevel) (Object) this, false))
            ci.cancel();
    }
}
