package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.EntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicBoolean;

public class EntityEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void onJoinWorldFiresWhenEntityIsAdded(GameTestHelper context) {
		ItemEntity entity = itemEntity(context, new BlockPos(1, 1, 1));
		AtomicBoolean joined = new AtomicBoolean(false);

		EntityEvents.ON_JOIN_WORLD.register((e, world, loadedFromDisk) -> {
			if (entity.getUUID() == e.getUUID()) {
				joined.set(true);
			}
			return true;
		});

		context.startSequence()
				.thenExecute(() -> context.assertTrue(context.getLevel().addFreshEntity(entity), "Expected entity add to succeed"))
				.thenExecute(() -> context.assertTrue(joined.get(), "Expected ON_JOIN_WORLD to fire from addFreshEntity"))
				.thenSucceed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void onJoinWorldCanCancelEntityAdd(GameTestHelper context) {
		ItemEntity entity = itemEntity(context, new BlockPos(1, 1, 1));
		AtomicBoolean cancelled = new AtomicBoolean(false);

		EntityEvents.ON_JOIN_WORLD.register((e, world, loadedFromDisk) -> {
			if (entity.getUUID() == e.getUUID()) {
				cancelled.set(true);
				return false;
			}
			return true;
		});

		context.startSequence()
				.thenExecute(() -> context.assertFalse(context.getLevel().addFreshEntity(entity), "Expected entity add to be cancelled"))
				.thenExecute(() -> context.assertTrue(cancelled.get(), "Expected ON_JOIN_WORLD callback to run"))
				.thenExecute(() -> context.assertTrue(context.getLevel().getEntity(entity.getUUID()) == null, "Expected cancelled entity not to be present"))
				.thenSucceed();
	}

	private static ItemEntity itemEntity(GameTestHelper context, BlockPos pos) {
		return new ItemEntity(context.getLevel(), context.absolutePos(pos).getX(), context.absolutePos(pos).getY(), context.absolutePos(pos).getZ(), new ItemStack(Items.DIRT));
	}
}
