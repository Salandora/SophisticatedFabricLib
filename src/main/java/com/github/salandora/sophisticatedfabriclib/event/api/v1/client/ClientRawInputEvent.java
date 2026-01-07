package com.github.salandora.sophisticatedfabriclib.event.api.v1.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;


@Environment(EnvType.CLIENT)
public interface ClientRawInputEvent {
    Event<MouseScrolled> MOUSE_SCROLLED = EventFactory.createArrayBacked(MouseScrolled.class, callbacks -> (client, deltaX, deltaY) -> {
        for (var event : callbacks) {
            var result = event.mouseScrolled(client, deltaX, deltaY);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    });

    @FunctionalInterface
    interface MouseScrolled {
        InteractionResult mouseScrolled(Minecraft client, double deltaX, double deltaY);
    }
}
