package com.github.salandora.sophisticatedfabriclib.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedfabriclib.util.Capabilities;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShulkerInvWrapperTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	private static void assertItemStack(ItemStack expected, ItemStack got) {
		if (got.getItem() != expected.getItem())
			throw new AssertionError("Expected item " + expected.getItem() + ", got: " + got.getItem());

		if (got.getCount() != expected.getCount())
			throw new AssertionError("Expected count " + expected.getCount() + ", got: " + got.getCount());
	}

	static ItemStack createShulkerBox() {
		return createShulkerBox(NonNullList.withSize(27, ItemStack.EMPTY));
	}

	static ItemStack createShulkerBox(NonNullList<ItemStack> stacks) {
		ItemStack shulkerBox = new ItemStack(Items.SHULKER_BOX);
		fillShulkerBox(shulkerBox, stacks);
		return shulkerBox;
	}

	static ItemStack fillShulkerBox(ItemStack shulkerBox, NonNullList<ItemStack> stacks) {
		CompoundTag tag = ContainerHelper.saveAllItems(new CompoundTag(), stacks);
		BlockItem.setBlockEntityData(shulkerBox, BlockEntityType.SHULKER_BOX, tag);
		return shulkerBox;
	}

	static NonNullList<ItemStack> getItemsInShulkerBox(ItemStack shulkerBox) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);
		CompoundTag tag = BlockItem.getBlockEntityData(shulkerBox);
		ContainerHelper.loadAllItems(tag, stacks);
		return stacks;
	}

	record TestCase(ItemStack input, boolean simulate, long expectedReturn, ItemStack expectedSlotItem) {
	}

	static Stream<TestCase> insertCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.STONE, 64), true, 64, ItemStack.EMPTY),
				new TestCase(new ItemStack(Items.STONE, 64), false, 64, new ItemStack(Items.STONE, 64)),

				new TestCase(new ItemStack(Items.EGG, 16), true, 16, ItemStack.EMPTY),
				new TestCase(new ItemStack(Items.EGG, 16), false, 16, new ItemStack(Items.EGG, 16)),

				new TestCase(new ItemStack(Items.EGG, 64), true, 64, ItemStack.EMPTY),
				new TestCase(new ItemStack(Items.EGG, 64), false, 64, new ItemStack(Items.EGG, 16))
		);
	}

	@ParameterizedTest
	@MethodSource("insertCases")
	void testInsert(TestCase c) {
		ItemStack shulkerBox = createShulkerBox();

		IItemHandler wrapper = Capabilities.ItemHandler.ITEM.find(shulkerBox, null);
		long inserted = wrapper.insert(c.input, c.simulate);

		NonNullList<ItemStack> stacks = getItemsInShulkerBox(shulkerBox);
		assertEquals(c.expectedReturn, inserted);
		assertItemStack(c.expectedSlotItem, stacks.get(0));
	}

	static Stream<TestCase> extractCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.STONE, 64), true, 64, new ItemStack(Items.STONE, 64)),
				new TestCase(new ItemStack(Items.STONE, 64), false, 64, ItemStack.EMPTY),

				new TestCase(new ItemStack(Items.STONE, 16), true, 16, new ItemStack(Items.STONE, 64)),
				new TestCase(new ItemStack(Items.STONE, 16), false, 16, new ItemStack(Items.STONE, 48))
		);
	}

	@ParameterizedTest
	@MethodSource("extractCases")
	void testExtract(TestCase c) {
		NonNullList<ItemStack> originalStacks = NonNullList.withSize(27, ItemStack.EMPTY);
		originalStacks.set(0, new ItemStack(Items.STONE, 64));

		ItemStack shulkerBox = createShulkerBox(originalStacks);

		IItemHandler wrapper = Capabilities.ItemHandler.ITEM.find(shulkerBox, null);
		long extracted = wrapper.extract(c.input, c.simulate);

		NonNullList<ItemStack> stacks = getItemsInShulkerBox(shulkerBox);
		assertEquals(c.expectedReturn, extracted);
		assertItemStack(c.expectedSlotItem, stacks.get(0));
	}
}