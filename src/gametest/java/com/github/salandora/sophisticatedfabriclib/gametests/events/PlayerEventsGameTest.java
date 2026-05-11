package com.github.salandora.sophisticatedfabriclib.gametests.events;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.PlayerEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerEventsGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void itemCraftedFiresWhenResultSlotIsTaken(GameTestHelper context) {
		Player player = context.makeMockPlayer(GameType.SURVIVAL);
		AtomicBoolean crafted = new AtomicBoolean(false);
		TransientCraftingContainer craftSlots = new TransientCraftingContainer(new DummyMenu(), 2, 2);
		ResultContainer resultSlots = new ResultContainer();
		ResultSlot resultSlot = new ResultSlot(player, craftSlots, resultSlots, 0, 0, 0);

		PlayerEvents.ITEM_CRAFTED.register((p, stack, craftMatrix) ->
				crafted.set(stack.is(Items.CRAFTING_TABLE) && craftMatrix.getContainerSize() == 4)
		);

		context.startSequence()
				.thenExecute(() -> resultSlot.onTake(player, new ItemStack(Items.CRAFTING_TABLE)))
				.thenExecute(() -> context.assertTrue(crafted.get(), "Expected ITEM_CRAFTED to fire from ResultSlot.onTake"))
				.thenSucceed();
	}

	private static class DummyMenu extends AbstractContainerMenu {
		private DummyMenu() {
			super(MenuType.CRAFTING, 0);
		}

		@Override
		public ItemStack quickMoveStack(Player player, int index) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean stillValid(Player player) {
			return true;
		}
	}
}
