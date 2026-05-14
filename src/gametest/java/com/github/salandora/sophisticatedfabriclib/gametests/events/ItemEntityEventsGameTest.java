package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.ItemEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicBoolean;

public class ItemEntityEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void canPickupFiresFromPlayerTouchAndCanCancelPickup(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		ItemEntity itemEntity = new ItemEntity(context.getLevel(), context.absolutePos(pos).getX(), context.absolutePos(pos).getY(), context.absolutePos(pos).getZ(), new ItemStack(Items.DIAMOND));
		AtomicBoolean pickupAttempt = new AtomicBoolean(false);

		ItemEntityEvents.CAN_PICKUP.register((player, e, stack) -> {
			if (itemEntity.getUUID() == e.getUUID()) {
				pickupAttempt.set(true);
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		context.startSequence()
				.thenExecute(() -> context.assertTrue(context.getLevel().addFreshEntity(itemEntity), "Expected item entity to be added"))
				.thenExecute(() -> itemEntity.playerTouch(context.makeMockPlayer()))
				.thenExecute(() -> context.assertTrue(pickupAttempt.get(), "Expected CAN_PICKUP to fire from ItemEntity.playerTouch"))
				.thenExecute(() -> context.assertFalse(itemEntity.isRemoved(), "Expected FAIL result to cancel pickup"))
				.thenSucceed();
	}
}
