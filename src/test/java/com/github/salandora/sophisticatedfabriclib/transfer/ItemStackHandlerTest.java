package com.github.salandora.sophisticatedfabriclib.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ItemStackHandlerTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	private static HolderLookup.Provider registryProvider() {
		return HolderLookup.Provider.create(Stream.of(BuiltInRegistries.REGISTRY.asLookup()));
	}

	static final class RestrictedItemStackHandler extends ItemStackHandler {
		private int changedSlot = -1;
		private int changedCount;

		RestrictedItemStackHandler(int size) {
			super(size);
		}

		@Override
		public int getSlotLimit(int slot) {
			validateSlotIndex(slot);
			return 4;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(Items.DIRT);
		}

		@Override
		protected void onContentsChanged(int slot) {
			changedSlot = slot;
			changedCount++;
		}
	}

	@Test
	void testInsertAndExtract() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack input = new ItemStack(Items.DIRT, 10);

		ItemStack remainder = handler.insertItem(0, input, false);
		assertEquals(ItemStack.EMPTY, remainder);
		assertEquals(10, handler.getStackInSlot(0).getCount());

		ItemStack extracted = handler.extractItem(0, 5, false);
		assertEquals(5, extracted.getCount());
		assertEquals(5, handler.getStackInSlot(0).getCount());
	}

	@Test
	void testInsertBeyondLimit() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack input = new ItemStack(Items.DIRT, 100);

		ItemStack remainder = handler.insertItem(0, input, false);
		assertTrue(remainder.getCount() > 0);
		assertEquals(Items.DIRT, remainder.getItem());
		assertEquals(input.getMaxStackSize(), handler.getStackInSlot(0).getCount());
	}

	@Test
	void testInsertSimulate() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack input = new ItemStack(Items.DIRT, 10);

		ItemStack remainder = handler.insertItem(0, input, true);
		assertEquals(ItemStack.EMPTY, remainder);
		assertEquals(0, handler.getStackInSlot(0).getCount());
	}

	@Test
	void testInsertEmptyStackReturnsEmptyWithoutValidatingSlot() {
		ItemStackHandler handler = new ItemStackHandler(1);

		assertEquals(ItemStack.EMPTY, handler.insertItem(99, ItemStack.EMPTY, false));
	}

	@Test
	void testInsertRejectedByItemValidityReturnsOriginalStack() {
		RestrictedItemStackHandler handler = new RestrictedItemStackHandler(1);
		ItemStack input = new ItemStack(Items.STONE, 3);

		ItemStack remainder = handler.insertItem(0, input, false);

		assertSame(input, remainder);
		assertTrue(handler.getStackInSlot(0).isEmpty());
		assertEquals(0, handler.changedCount);
	}

	@Test
	void testInsertMergesMatchingStackAndReturnsRemainderAtSlotLimit() {
		RestrictedItemStackHandler handler = new RestrictedItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 2));
		handler.changedCount = 0;

		ItemStack remainder = handler.insertItem(0, new ItemStack(Items.DIRT, 5), false);

		assertEquals(4, handler.getStackInSlot(0).getCount());
		assertEquals(3, remainder.getCount());
		assertEquals(Items.DIRT, remainder.getItem());
		assertEquals(1, handler.changedCount);
		assertEquals(0, handler.changedSlot);
	}

	@Test
	void testInsertDifferentItemIntoOccupiedSlotReturnsOriginalStack() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 2));
		ItemStack input = new ItemStack(Items.STONE, 5);

		ItemStack remainder = handler.insertItem(0, input, false);

		assertSame(input, remainder);
		assertEquals(Items.DIRT, handler.getStackInSlot(0).getItem());
		assertEquals(2, handler.getStackInSlot(0).getCount());
	}

	@Test
	void testInsertIntoFullSlotReturnsOriginalStack() {
		RestrictedItemStackHandler handler = new RestrictedItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 4));
		handler.changedCount = 0;
		ItemStack input = new ItemStack(Items.DIRT, 1);

		ItemStack remainder = handler.insertItem(0, input, false);

		assertSame(input, remainder);
		assertEquals(4, handler.getStackInSlot(0).getCount());
		assertEquals(0, handler.changedCount);
	}

	@Test
	void testExtractSimulate() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 10));

		ItemStack extracted = handler.extractItem(0, 5, true);
		assertEquals(5, extracted.getCount());
		assertEquals(10, handler.getStackInSlot(0).getCount());
	}

	@Test
	void testExtractZeroAmountReturnsEmptyWithoutValidatingSlot() {
		ItemStackHandler handler = new ItemStackHandler(1);

		assertEquals(ItemStack.EMPTY, handler.extractItem(99, 0, false));
	}

	@Test
	void testExtractEmptySlotReturnsEmpty() {
		ItemStackHandler handler = new ItemStackHandler(1);

		assertEquals(ItemStack.EMPTY, handler.extractItem(0, 5, false));
	}

	@Test
	void testExtractAllClearsSlotAndFiresChange() {
		RestrictedItemStackHandler handler = new RestrictedItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 3));
		handler.changedCount = 0;

		ItemStack extracted = handler.extractItem(0, 10, false);

		assertEquals(3, extracted.getCount());
		assertEquals(Items.DIRT, extracted.getItem());
		assertTrue(handler.getStackInSlot(0).isEmpty());
		assertEquals(1, handler.changedCount);
		assertEquals(0, handler.changedSlot);
	}

	@Test
	void testExtractAllSimulateReturnsCopyAndLeavesSlotUntouched() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack stored = new ItemStack(Items.DIRT, 3);
		handler.setStackInSlot(0, stored);

		ItemStack extracted = handler.extractItem(0, 10, true);

		assertNotSame(stored, extracted);
		assertEquals(3, extracted.getCount());
		assertSame(stored, handler.getStackInSlot(0));
	}

	@Test
	void testSetAndGetStack() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack stack = new ItemStack(Items.STONE, 3);
		handler.setStackInSlot(0, stack);
		assertEquals(stack, handler.getStackInSlot(0));
	}

	@Test
	void testConstructorsSlotCountLimitsAndSetSize() {
		ItemStackHandler defaultHandler = new ItemStackHandler();
		assertEquals(1, defaultHandler.getSlotCount());

		NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
		stacks.set(1, new ItemStack(Items.STONE, 3));
		ItemStackHandler fromList = new ItemStackHandler(stacks);
		assertEquals(2, fromList.getSlotCount());
		assertEquals(Items.STONE, fromList.getStackInSlot(1).getItem());

		assertEquals(Item.ABSOLUTE_MAX_STACK_SIZE, fromList.getSlotLimit(0));
		assertEquals(16, fromList.getStackLimit(0, new ItemStack(Items.ENDER_PEARL, 16)));
		assertTrue(fromList.isItemValid(0, new ItemStack(Items.DIRT)));

		fromList.setSize(3);
		assertEquals(3, fromList.getSlotCount());
		assertTrue(fromList.getStackInSlot(1).isEmpty());
	}

	@Test
	void testInvalidSlotInsertExtract() {
		ItemStackHandler handler = new ItemStackHandler(1);
		assertThrows(RuntimeException.class, () -> handler.insertItem(1, new ItemStack(Items.DIRT), false));
		assertThrows(RuntimeException.class, () -> handler.extractItem(1, 1, false));
		assertThrows(RuntimeException.class, () -> handler.getStackInSlot(1));
		assertThrows(RuntimeException.class, () -> handler.setStackInSlot(1, ItemStack.EMPTY));
	}

	@Test
	void testSerializeDeserialize() {
		ItemStackHandler handler = new ItemStackHandler(2);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 4));
		handler.setStackInSlot(1, new ItemStack(Items.STONE, 6));

		HolderLookup.Provider provider = registryProvider();

		CompoundTag tag = handler.serializeNBT(provider);

		ItemStackHandler loaded = new ItemStackHandler(0);
		loaded.deserializeNBT(provider, tag);

		assertEquals(2, loaded.getSlotCount());
		assertEquals(4, loaded.getStackInSlot(0).getCount());
		assertEquals(Items.DIRT, loaded.getStackInSlot(0).getItem());
		assertEquals(6, loaded.getStackInSlot(1).getCount());
		assertEquals(Items.STONE, loaded.getStackInSlot(1).getItem());
	}

	@Test
	void testSerializeSkipsEmptySlotsAndStoresSize() {
		ItemStackHandler handler = new ItemStackHandler(3);
		handler.setStackInSlot(1, new ItemStack(Items.DIRT, 4));

		CompoundTag tag = handler.serializeNBT(registryProvider());
		ListTag items = tag.getList("Items", CompoundTag.TAG_COMPOUND);

		assertEquals(3, tag.getInt("Size"));
		assertEquals(1, items.size());
		assertEquals(1, items.getCompound(0).getInt("Slot"));
	}

	@Test
	void testDeserializeWithoutSizeKeepsCurrentSizeAndIgnoresOutOfRangeSlots() {
		HolderLookup.Provider provider = registryProvider();
		ItemStackHandler source = new ItemStackHandler(3);
		source.setStackInSlot(0, new ItemStack(Items.DIRT, 2));
		source.setStackInSlot(2, new ItemStack(Items.STONE, 5));

		CompoundTag tag = source.serializeNBT(provider);
		tag.remove("Size");

		ItemStackHandler loaded = new ItemStackHandler(1);
		loaded.deserializeNBT(provider, tag);

		assertEquals(1, loaded.getSlotCount());
		assertEquals(Items.DIRT, loaded.getStackInSlot(0).getItem());
		assertEquals(2, loaded.getStackInSlot(0).getCount());
	}

	@Test
	void testSetStackInSlotFiresChangeNotification() {
		RestrictedItemStackHandler handler = new RestrictedItemStackHandler(1);

		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 1));

		assertEquals(1, handler.changedCount);
		assertEquals(0, handler.changedSlot);
	}
}
