package com.github.salandora.sophisticatedfabriclib.event.api.v1.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface LivingEntityEvents {
	Event<Drops> DROPS = EventFactory.createArrayBacked(Drops.class, callbacks -> (target, source, drops, recentlyHit) -> {
		for (Drops callback : callbacks) {
			if (callback.onLivingEntityDrops(target, source, drops, recentlyHit)) {
				return true;
			}
		}

		return false;
	});

	@FunctionalInterface
	interface Drops {
		boolean onLivingEntityDrops(LivingEntity target, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit);
	}
}
