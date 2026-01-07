package com.github.salandora.sophisticatedfabriclib.event.api.v1.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

@Environment(EnvType.CLIENT)
public interface ClientLifecycleEvents {
    Event<Load> CLIENT_LEVEL_LOAD = EventFactory.createArrayBacked(Load.class, callback -> (client, world) -> {
       for(Load event : callback) {
           event.onWorldLoad(client, world);
       }
    });

    @FunctionalInterface
    interface Load {
        void onWorldLoad(Minecraft client, ClientLevel world);
    }
}
