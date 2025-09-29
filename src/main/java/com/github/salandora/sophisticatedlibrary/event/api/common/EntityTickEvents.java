package com.github.salandora.sophisticatedlibrary.event.api.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface EntityTickEvents {
	Event<Tick> POST = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
        for (Tick callback : callbacks) {
            callback.onLivingEntityTick(entity);
        }
    });

	@FunctionalInterface
    interface Tick {
        void onLivingEntityTick(Entity entity);
    }
}
