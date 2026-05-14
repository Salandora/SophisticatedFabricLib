package com.github.salandora.sophisticatedfabriclib.tests.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FabricItemStorageWrapperTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void genericStorageWrapperExposesFallbackItemHandlerSurface() {
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(new SimpleItemStorage(64));

		assertEquals(1, wrapper.getSlotCount());
		assertSame(ItemStack.EMPTY, wrapper.getStackInSlot(0));
		assertEquals(64, wrapper.getSlotLimit(0));
		assertTrue(wrapper.isItemValid(0, new ItemStack(Items.DIRT)));
		assertSame(ItemStack.EMPTY, wrapper.extractItem(0, 1, false));
	}

	@Test
	void genericInsertDoesNotReplaceItems() {
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(new SimpleItemStorage(64));

		wrapper.setStackInSlot(0, new ItemStack(Items.STONE));
		ItemStack input = new ItemStack(Items.DIRT, 4);
		assertTrue(wrapper.insertItem(0, input, false).isEmpty());
	}

	@Test
	void genericInsertReturnsZeroForEmptyStackWithoutTouchingStorage() {
		SimpleItemStorage storage = new SimpleItemStorage(64);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(0, wrapper.insert(ItemStack.EMPTY, false));
		assertTrue(storage.stack.isEmpty());
		assertEquals(0, storage.commits);
	}

	@Test
	void genericInsertSimulatesAndExecutesThroughStorageTransactions() {
		SimpleItemStorage storage = new SimpleItemStorage(64);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(10, wrapper.insert(new ItemStack(Items.DIRT, 10), true));
		assertTrue(storage.stack.isEmpty());
		assertEquals(0, storage.commits);

		assertEquals(10, wrapper.insert(new ItemStack(Items.DIRT, 10), false));
		assertEquals(Items.DIRT, storage.stack.getItem());
		assertEquals(10, storage.stack.getCount());
		assertEquals(1, storage.commits);
	}

	@Test
	void genericInsertClampsToCapacityAndRejectsDifferentItem() {
		SimpleItemStorage storage = new SimpleItemStorage(12);
		storage.stack = new ItemStack(Items.DIRT, 10);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(2, wrapper.insert(new ItemStack(Items.DIRT, 10), false));
		assertEquals(12, storage.stack.getCount());

		assertEquals(0, wrapper.insert(new ItemStack(Items.STONE, 1), false));
		assertEquals(Items.DIRT, storage.stack.getItem());
		assertEquals(12, storage.stack.getCount());
	}

	@Test
	void genericExtractReturnsZeroForEmptyStackWithoutTouchingStorage() {
		SimpleItemStorage storage = new SimpleItemStorage(64);
		storage.stack = new ItemStack(Items.DIRT, 10);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(0, wrapper.extract(ItemStack.EMPTY, false));
		assertEquals(10, storage.stack.getCount());
		assertEquals(0, storage.commits);
	}

	@Test
	void genericExtractSimulatesAndExecutesThroughStorageTransactions() {
		SimpleItemStorage storage = new SimpleItemStorage(64);
		storage.stack = new ItemStack(Items.DIRT, 10);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(4, wrapper.extract(new ItemStack(Items.DIRT, 4), true));
		assertEquals(10, storage.stack.getCount());
		assertEquals(0, storage.commits);

		assertEquals(4, wrapper.extract(new ItemStack(Items.DIRT, 4), false));
		assertEquals(6, storage.stack.getCount());
		assertEquals(1, storage.commits);
	}

	@Test
	void genericExtractRejectsDifferentItemAndClampsToStoredAmount() {
		SimpleItemStorage storage = new SimpleItemStorage(64);
		storage.stack = new ItemStack(Items.DIRT, 3);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(storage);

		assertEquals(0, wrapper.extract(new ItemStack(Items.STONE, 10), false));
		assertEquals(3, storage.stack.getCount());

		assertEquals(3, wrapper.extract(new ItemStack(Items.DIRT, 10), false));
		assertTrue(storage.stack.isEmpty());
	}

	@Test
	void slottedWrapperExposesSlots() {
		ItemStackHandler handler = new ItemStackHandler(2);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 5));
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(FabricItemHandlerWrapper.of(handler));

		assertEquals(2, wrapper.getSlotCount());
		assertEquals(Items.DIRT, wrapper.getStackInSlot(0).getItem());
		assertEquals(5, wrapper.getStackInSlot(0).getCount());
		assertSame(ItemStack.EMPTY, wrapper.getStackInSlot(1));
		assertEquals(new ItemStack(Items.DIRT).getMaxStackSize(), wrapper.getSlotLimit(0));
	}

	@Test
	void slottedInsertItemReturnsRemainderAndHonorsSimulation() {
		ItemStackHandler handler = new ItemStackHandler(1);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(FabricItemHandlerWrapper.of(handler));

		ItemStack simulatedRemainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 10), true);
		assertTrue(simulatedRemainder.isEmpty());
		assertTrue(handler.getStackInSlot(0).isEmpty());

		simulatedRemainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 70), true);
		assertEquals(6, simulatedRemainder.getCount());
		assertEquals(Items.DIRT, simulatedRemainder.getItem());
		assertTrue(handler.getStackInSlot(0).isEmpty());

		ItemStack remainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 70), false);
		assertEquals(6, remainder.getCount());
		assertEquals(Items.DIRT, remainder.getItem());
		assertEquals(64, handler.getStackInSlot(0).getCount());
	}

	@Test
	void slottedInsertItemReturnsInputForEmptyStack() {
		ItemStackHandler handler = new ItemStackHandler(1);
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(FabricItemHandlerWrapper.of(handler));

		assertSame(ItemStack.EMPTY, wrapper.insertItem(0, ItemStack.EMPTY, false));
	}

	@Test
	void slottedExtractItemReturnsEmptyForZeroAmountAndHonorsSimulation() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 10));
		FabricItemStorageWrapper wrapper = FabricItemStorageWrapper.of(FabricItemHandlerWrapper.of(handler));

		assertSame(ItemStack.EMPTY, wrapper.extractItem(0, 0, false));

		ItemStack simulated = wrapper.extractItem(0, 4, true);
		assertEquals(Items.DIRT, simulated.getItem());
		assertEquals(4, simulated.getCount());
		assertEquals(10, handler.getStackInSlot(0).getCount());

		ItemStack extracted = wrapper.extractItem(0, 4, false);
		assertEquals(Items.DIRT, extracted.getItem());
		assertEquals(4, extracted.getCount());
		assertEquals(6, handler.getStackInSlot(0).getCount());
	}

	private static class SimpleItemStorage extends SingleStackStorage {
		private final int capacity;
		private ItemStack stack = ItemStack.EMPTY;
		private int commits;

		private SimpleItemStorage(int capacity) {
			this.capacity = capacity;
		}

		@Override
		protected ItemStack getStack() {
			return this.stack;
		}

		@Override
		protected void setStack(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		protected int getCapacity(ItemVariant itemVariant) {
			return capacity;
		}

		@Override
		protected void onFinalCommit() {
			commits++;
		}
	}
}
