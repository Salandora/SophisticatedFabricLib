package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.MobSpawnEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;

import java.util.concurrent.atomic.AtomicBoolean;

public class MobSpawnEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void afterFinalizeSpawnFiresWhenMobIsSpawned(GameTestHelper context) {
		BlockPos pos = context.absolutePos(new BlockPos(1, 1, 1));
		AtomicBoolean finalized = new AtomicBoolean(false);

		MobSpawnEvents.AFTER_FINALIZE_SPAWN.register(event -> {
			if (event.getEntity().blockPosition().equals(pos)) {
				finalized.set(event.getLevel() != null
						&& event.getDifficulty() != null
						&& event.getMobSpawnType() == MobSpawnType.COMMAND);
			}
		});

		context.startSequence()
				.thenExecute(() -> {
					Zombie zombie = EntityType.ZOMBIE.spawn(context.getLevel(), pos, MobSpawnType.COMMAND);
					context.assertTrue(zombie != null, "Expected zombie spawn to succeed");
				})
				.thenWaitUntil(() -> context.assertTrue(finalized.get(), "Expected AFTER_FINALIZE_SPAWN to fire from EntityType.spawn"))
				.thenSucceed();
	}
}
