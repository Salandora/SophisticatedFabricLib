package com.github.salandora.sophisticatedfabriclib.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FabricItemHandlerWrapperTest {
	private static ItemVariant dirt;
	private static ItemVariant stone;

	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		dirt = ItemVariant.of(Items.DIRT);
		stone = ItemVariant.of(Items.STONE);
	}

	@Test
	void ofReturnsNullForNullHandler() {
		assertNull(FabricItemHandlerWrapper.of(null));
	}

	@Test
	void accessorsAndCapabilitiesDelegateToWrappedHandler() {
		ItemStackHandler handler = new ItemStackHandler(2);
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		assertNotNull(wrapper);
		assertSame(handler, wrapper.getHandler());
		assertEquals(2, wrapper.getSlotCount());
		assertTrue(wrapper.supportsInsertion());
		assertTrue(wrapper.supportsExtraction());
	}

	@Test
	void insertCommitsOnlyWhenTransactionCommits() {
		ItemStackHandler handler = new ItemStackHandler(1);
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(10, wrapper.insert(dirt, 10, transaction));
		}

		assertTrue(handler.getStackInSlot(0).isEmpty());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(10, wrapper.insert(dirt, 10, transaction));
			transaction.commit();
		}

		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertEquals(10, handler.getStackInSlot(0).getCount());
	}

	@Test
	void insertClampsToStackLimitAndRejectsDifferentItem() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 60));
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(4, wrapper.insert(dirt, 10, transaction));
			transaction.commit();
		}

		assertEquals(64, handler.getStackInSlot(0).getCount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, wrapper.insert(stone, 1, transaction));
			transaction.commit();
		}

		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertEquals(64, handler.getStackInSlot(0).getCount());
	}

	@Test
	void extractCommitsOnlyWhenTransactionCommits() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 10));
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(4, wrapper.extract(dirt, 4, transaction));
		}

		assertEquals(10, handler.getStackInSlot(0).getCount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(4, wrapper.extract(dirt, 4, transaction));
			transaction.commit();
		}

		assertEquals(6, handler.getStackInSlot(0).getCount());
	}

	@Test
	void extractRejectsDifferentItemAndClampsToStoredAmount() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 3));
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, wrapper.extract(stone, 10, transaction));
			assertEquals(3, wrapper.extract(dirt, 10, transaction));
			transaction.commit();
		}

		assertTrue(handler.getStackInSlot(0).isEmpty());
	}

	@Test
	void iteratorExposesViewsForEverySlot() {
		ItemStackHandler handler = new ItemStackHandler(2);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 7));
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);

		List<StorageView<ItemVariant>> views = Lists.newArrayList(wrapper.iterator());

		assertEquals(2, views.size());
		StorageView<ItemVariant> dirtView = views.stream().filter(view -> !view.isResourceBlank()).findFirst().orElseThrow();
		StorageView<ItemVariant> emptyView = views.stream().filter(StorageView::isResourceBlank).findFirst().orElseThrow();

		assertEquals(dirt, dirtView.getResource());
		assertEquals(7, dirtView.getAmount());
		assertEquals(Items.DIRT.getDefaultMaxStackSize(), dirtView.getCapacity());
		assertEquals(ItemVariant.blank(), emptyView.getResource());
		assertEquals(0, emptyView.getAmount());
		assertEquals(Items.AIR.getDefaultMaxStackSize(), emptyView.getCapacity());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, emptyView.extract(dirt, 1, transaction));
			transaction.commit();
		}
	}

	@Test
	void viewExtractionUsesSlotTransactionSnapshot() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 8));
		StorageView<ItemVariant> view = FabricItemHandlerWrapper.of(handler).iterator().next();

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, view.extract(stone, 3, transaction));
			assertEquals(3, view.extract(dirt, 3, transaction));
		}

		assertEquals(8, handler.getStackInSlot(0).getCount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(3, view.extract(dirt, 3, transaction));
			transaction.commit();
		}

		assertEquals(5, handler.getStackInSlot(0).getCount());
	}

	@Test
	void slotStorageInsertsExtractsAndRollsBack() {
		ItemStackHandler handler = new ItemStackHandler(1);
		FabricItemHandlerWrapper wrapper = FabricItemHandlerWrapper.of(handler);
		SingleSlotStorage<ItemVariant> slot = wrapper.getSlot(0);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(5, slot.insert(dirt, 5, transaction));
		}

		assertTrue(handler.getStackInSlot(0).isEmpty());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(5, slot.insert(dirt, 5, transaction));
			transaction.commit();
		}

		assertEquals(5, handler.getStackInSlot(0).getCount());
		assertEquals(dirt, slot.getResource());
		assertEquals(5, slot.getAmount());
		assertFalse(slot.isResourceBlank());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(2, slot.extract(dirt, 2, transaction));
		}

		assertEquals(5, handler.getStackInSlot(0).getCount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(2, slot.extract(dirt, 2, transaction));
			transaction.commit();
		}

		assertEquals(3, handler.getStackInSlot(0).getCount());
	}
}
