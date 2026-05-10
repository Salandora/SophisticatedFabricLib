package com.github.salandora.sophisticatedfabriclib.tests.transfer;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component.SophisticatedMutableDataComponentHolder;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ComponentItemHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentItemHandlerTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void constructorRejectsSizesAboveContainerComponentLimit() {
		assertThrows(IllegalArgumentException.class, () -> new TestComponentItemHandler(parent(), 257));
	}

	@Test
	void reportsSlotCountLimitAndDefaultValidity() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 3);

		assertEquals(3, handler.getSlotCount());
		assertEquals(Item.ABSOLUTE_MAX_STACK_SIZE, handler.getSlotLimit(0));
		assertTrue(handler.isItemValid(0, new ItemStack(Items.DIRT)));
		assertFalse(handler.isItemValid(0, new ItemStack(Items.SHULKER_BOX)));
	}

	@Test
	void getStackInSlotReturnsEmptyWhenComponentOrSlotIsMissing() {
		ItemStack parent = parent();
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 3);

		assertSame(ItemStack.EMPTY, handler.getStackInSlot(0));

		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT)));

		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertSame(ItemStack.EMPTY, handler.getStackInSlot(2));
	}

	@Test
	void getStackInSlotReturnsCopy() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 3)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		ItemStack returned = handler.getStackInSlot(0);
		returned.setCount(1);

		assertEquals(3, handler.getStackInSlot(0).getCount());
	}

	@Test
	void setStackInSlotWritesComponentAndReportsChange() {
		ItemStack parent = parent();
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 2);

		handler.setStackInSlot(1, new ItemStack(Items.DIRT, 4));

		assertEquals(Items.DIRT, handler.getStackInSlot(1).getItem());
		assertEquals(4, handler.getStackInSlot(1).getCount());
		assertEquals(1, handler.changedSlot);
		assertTrue(handler.oldStack.isEmpty());
		assertEquals(Items.DIRT, handler.newStack.getItem());
		assertEquals(4, handler.newStack.getCount());
	}

	@Test
	void setStackInSlotDoesNotWriteWhenStackAlreadyMatches() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 4)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 4));

		assertEquals(0, handler.changedCount);
	}

	@Test
	void setStackInSlotRejectsInvalidItems() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 1);

		assertThrows(RuntimeException.class, () -> handler.setStackInSlot(0, new ItemStack(Items.SHULKER_BOX)));
		assertSame(ItemStack.EMPTY, handler.getStackInSlot(0));
	}

	@Test
	void insertEmptyStackReturnsEmptyAfterSlotValidation() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 1);

		assertSame(ItemStack.EMPTY, handler.insertItem(0, ItemStack.EMPTY, false));
		assertThrows(RuntimeException.class, () -> handler.insertItem(1, ItemStack.EMPTY, false));
	}

	@Test
	void insertRejectsInvalidOrDifferentItems() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 3)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		ItemStack invalid = new ItemStack(Items.SHULKER_BOX);
		assertSame(invalid, handler.insertItem(0, invalid, false));

		ItemStack different = new ItemStack(Items.STONE, 2);
		assertSame(different, handler.insertItem(0, different, false));
		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertEquals(3, handler.getStackInSlot(0).getCount());
	}

	@Test
	void insertSimulatesAndExecutesIntoEmptySlot() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 1);

		ItemStack simulatedRemainder = handler.insertItem(0, new ItemStack(Items.DIRT, 5), true);
		assertTrue(simulatedRemainder.isEmpty());
		assertSame(ItemStack.EMPTY, handler.getStackInSlot(0));
		assertEquals(0, handler.changedCount);

		ItemStack remainder = handler.insertItem(0, new ItemStack(Items.DIRT, 5), false);
		assertTrue(remainder.isEmpty());
		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertEquals(5, handler.getStackInSlot(0).getCount());
		assertEquals(1, handler.changedCount);
	}

	@Test
	void insertMergesAndReturnsRemainderAtStackLimit() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 60)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		ItemStack remainder = handler.insertItem(0, new ItemStack(Items.DIRT, 10), false);

		assertEquals(Items.DIRT, remainder.getItem());
		assertEquals(6, remainder.getCount());
		assertEquals(64, handler.getStackInSlot(0).getCount());
	}

	@Test
	void insertReturnsOriginalWhenSlotHasNoSpace() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 64)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);
		ItemStack input = new ItemStack(Items.DIRT, 1);

		assertSame(input, handler.insertItem(0, input, false));
		assertEquals(64, handler.getStackInSlot(0).getCount());
	}

	@Test
	void extractReturnsEmptyForZeroAmountOrEmptySlot() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 1);

		assertSame(ItemStack.EMPTY, handler.extractItem(0, 0, false));
		assertSame(ItemStack.EMPTY, handler.extractItem(0, 5, false));
		assertThrows(RuntimeException.class, () -> handler.extractItem(1, 0, false));
	}

	@Test
	void extractSimulatesAndExecutes() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 10)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		ItemStack simulated = handler.extractItem(0, 4, true);
		assertEquals(Items.DIRT, simulated.getItem());
		assertEquals(4, simulated.getCount());
		assertEquals(10, handler.getStackInSlot(0).getCount());
		assertEquals(0, handler.changedCount);

		ItemStack extracted = handler.extractItem(0, 4, false);
		assertEquals(Items.DIRT, extracted.getItem());
		assertEquals(4, extracted.getCount());
		assertEquals(6, handler.getStackInSlot(0).getCount());
		assertEquals(1, handler.changedCount);
	}

	@Test
	void extractClampsToStoredAmount() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(new ItemStack(Items.DIRT, 3)));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 1);

		ItemStack extracted = handler.extractItem(0, 10, false);

		assertEquals(Items.DIRT, extracted.getItem());
		assertEquals(3, extracted.getCount());
		assertTrue(handler.getStackInSlot(0).isEmpty());
	}

	@Test
	void updateDoesNotTruncateExistingComponentSlotsBeyondHandlerSize() {
		ItemStack parent = parent();
		parent.set(DataComponents.CONTAINER, contents(
				new ItemStack(Items.DIRT, 1),
				new ItemStack(Items.STONE, 2),
				new ItemStack(Items.COBBLESTONE, 3),
				new ItemStack(Items.GRANITE, 4)
		));
		TestComponentItemHandler handler = new TestComponentItemHandler(parent, 2);

		handler.setStackInSlot(1, new ItemStack(Items.SAND, 5));

		ItemContainerContents stored = parent.get(DataComponents.CONTAINER);
		assertNotNull(stored);
		assertEquals(4, slots(stored));
		assertEquals(Items.SAND, stackInSlot(stored, 1).getItem());
		assertEquals(Items.COBBLESTONE, stackInSlot(stored, 2).getItem());
		assertEquals(Items.GRANITE, stackInSlot(stored, 3).getItem());
	}

	@Test
	void invalidSlotsThrow() {
		TestComponentItemHandler handler = new TestComponentItemHandler(parent(), 1);

		assertThrows(RuntimeException.class, () -> handler.getStackInSlot(-1));
		assertThrows(RuntimeException.class, () -> handler.getStackInSlot(1));
		assertThrows(RuntimeException.class, () -> handler.setStackInSlot(1, new ItemStack(Items.DIRT)));
		assertThrows(RuntimeException.class, () -> handler.insertItem(1, new ItemStack(Items.DIRT), false));
		assertThrows(RuntimeException.class, () -> handler.extractItem(1, 1, false));
	}

	private static ItemStack parent() {
		return new ItemStack(Items.CHEST);
	}

	private static ItemContainerContents contents(ItemStack... stacks) {
		NonNullList<ItemStack> list = NonNullList.withSize(stacks.length, ItemStack.EMPTY);
		for (int i = 0; i < stacks.length; i++) {
			list.set(i, stacks[i]);
		}
		return ItemContainerContents.fromItems(list);
	}

	private static int slots(ItemContainerContents contents) {
		return contents.sophisticatedFabricLibrary_getSlots();
	}

	private static ItemStack stackInSlot(ItemContainerContents contents, int slot) {
		return contents.sophisticatedFabricLibrary_getStackInSlot(slot);
	}

	private static class TestComponentItemHandler extends ComponentItemHandler {
		private int changedSlot = -1;
		private int changedCount;
		private ItemStack oldStack = ItemStack.EMPTY;
		private ItemStack newStack = ItemStack.EMPTY;

		private TestComponentItemHandler(ItemStack parent, int size) {
			super((SophisticatedMutableDataComponentHolder) (Object) parent, DataComponents.CONTAINER, size);
		}

		@Override
		protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
			this.changedSlot = slot;
			this.changedCount++;
			this.oldStack = oldStack;
			this.newStack = newStack;
		}
	}
}
