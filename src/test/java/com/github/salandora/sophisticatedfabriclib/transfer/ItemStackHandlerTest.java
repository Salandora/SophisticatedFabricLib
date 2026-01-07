package com.github.salandora.sophisticatedfabriclib.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
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
	void testExtractSimulate() {
		ItemStackHandler handler = new ItemStackHandler(1);
		handler.setStackInSlot(0, new ItemStack(Items.DIRT, 10));

		ItemStack extracted = handler.extractItem(0, 5, true);
		assertEquals(5, extracted.getCount());
		assertEquals(10, handler.getStackInSlot(0).getCount());
	}

	@Test
	void testSetAndGetStack() {
		ItemStackHandler handler = new ItemStackHandler(1);
		ItemStack stack = new ItemStack(Items.STONE, 3);
		handler.setStackInSlot(0, stack);
		assertEquals(stack, handler.getStackInSlot(0));
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

		HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(BuiltInRegistries.REGISTRY.asLookup()));

		CompoundTag tag = handler.serializeNBT(provider);

		ItemStackHandler loaded = new ItemStackHandler(0);
		loaded.deserializeNBT(provider, tag);

		assertEquals(2, loaded.getSlotCount());
		assertEquals(4, loaded.getStackInSlot(0).getCount());
		assertEquals(Items.DIRT, loaded.getStackInSlot(0).getItem());
		assertEquals(6, loaded.getStackInSlot(1).getCount());
		assertEquals(Items.STONE, loaded.getStackInSlot(1).getItem());
	}
}
