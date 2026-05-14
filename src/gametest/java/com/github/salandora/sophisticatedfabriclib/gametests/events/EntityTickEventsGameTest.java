package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.EntityTickEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityTickEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void postFiresAfterEntityTicksInServerLevel(GameTestHelper context) {
		ArmorStand armorStand = new ArmorStand(context.getLevel(), context.absolutePos(new BlockPos(1, 1, 1)).getX(), context.absolutePos(new BlockPos(1, 1, 1)).getY(), context.absolutePos(new BlockPos(1, 1, 1)).getZ());
		AtomicInteger ticks = new AtomicInteger();
		EntityTickEvents.POST.register(e -> {
			if (armorStand.getUUID() == e.getUUID()) {
				ticks.incrementAndGet();
			}
		});

		context.startSequence()
				.thenExecute(() -> context.assertTrue(context.getLevel().addFreshEntity(armorStand), "Expected armor stand to be added"))
				.thenExecuteAfter(1, () -> context.assertTrue(ticks.get() > 0, "Expected EntityTickEvents.POST to fire from the server tick loop"))
				.thenSucceed();
	}
}
