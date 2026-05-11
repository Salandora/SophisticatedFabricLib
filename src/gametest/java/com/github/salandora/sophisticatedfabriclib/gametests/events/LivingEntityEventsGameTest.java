package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.LivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

import java.util.concurrent.atomic.AtomicBoolean;

public class LivingEntityEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void dropsFiresWhenLivingEntityDiesAndCanCancelDrops(GameTestHelper context) {
		Zombie zombie = EntityType.ZOMBIE.create(context.getLevel());
		context.assertTrue(zombie != null, "Expected zombie to be created");
		zombie.setNoAi(true);
		zombie.setPos(context.absoluteVec(new BlockPos(1, 1, 1).getCenter()));
		AtomicBoolean dropped = new AtomicBoolean(false);

		LivingEntityEvents.DROPS.register((target, source, drops, recentlyHit) -> {
			if (zombie.getUUID() == target.getUUID()) {
				dropped.set(true);
				return true;
			}
			return false;
		});

		context.startSequence()
				.thenExecute(() -> context.assertTrue(context.getLevel().addFreshEntity(zombie), "Expected zombie to be added"))
				.thenExecute(() -> zombie.hurt(context.getLevel().damageSources().generic(), Float.MAX_VALUE))
				.thenWaitUntil(() -> context.assertTrue(dropped.get(), "Expected LivingEntityEvents.DROPS to fire from death loot handling"))
				.thenSucceed();
	}
}
