package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MutableContainerItemContextTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	public static ItemStack createItemStack(Item item) {
		return createItemStack(item, 1);
	}
	public static ItemStack createItemStack(Item item, int count) {
		ItemStack itemStack = new ItemStack(item, count);
		itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
		return itemStack;
	}

	record TestCase(ItemStack input, FluidVariant fluid, long amount, boolean commit,
					ItemStack expectedItem, long expectedFluid) {}

	static Stream<TestCase> insertCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, false, new ItemStack(Items.BUCKET), FluidConstants.BUCKET),
				new TestCase(new ItemStack(Items.BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, true, new ItemStack(Items.WATER_BUCKET), FluidConstants.BUCKET),
				new TestCase(new ItemStack(Items.WATER_BUCKET), FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET, true, new ItemStack(Items.WATER_BUCKET), 0),
				new TestCase(new ItemStack(Items.BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET * 2, true, new ItemStack(Items.WATER_BUCKET), FluidConstants.BUCKET),

				new TestCase(new ItemStack(Items.GLASS_BOTTLE), FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, false, new ItemStack(Items.GLASS_BOTTLE), FluidConstants.BOTTLE),
				new TestCase(new ItemStack(Items.GLASS_BOTTLE), FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, true, createItemStack(Items.POTION), FluidConstants.BOTTLE)
		);
	}

	@ParameterizedTest
	@MethodSource("insertCases")
	void testInsert(TestCase c) {
		MutableContainerItemContext ctx = new MutableContainerItemContext(c.input());
		Storage<FluidVariant> storage = FluidStorage.ITEM.find(c.input(), ctx);
		assertNotNull(storage);

		try (Transaction tx = Transaction.openOuter()) {
			long inserted = storage.insert(c.fluid(), c.amount(), tx);
			assertEquals(c.expectedFluid(), inserted);
			if (c.commit()) tx.commit();
		}

		assertEquals(c.expectedItem().getItem(), ctx.getItemVariant().getItem());
	}

	static Stream<TestCase> extractCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.WATER_BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, true, new ItemStack(Items.BUCKET), FluidConstants.BUCKET),
				new TestCase(new ItemStack(Items.WATER_BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, false, new ItemStack(Items.WATER_BUCKET), FluidConstants.BUCKET),
				new TestCase(new ItemStack(Items.WATER_BUCKET), FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET, true, new ItemStack(Items.WATER_BUCKET), 0),
				new TestCase(new ItemStack(Items.WATER_BUCKET), FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET * 2, true, new ItemStack(Items.BUCKET), FluidConstants.BUCKET),

				new TestCase(createItemStack(Items.POTION), FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, false, createItemStack(Items.POTION), FluidConstants.BOTTLE),
				new TestCase(createItemStack(Items.POTION), FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE, true, new ItemStack(Items.GLASS_BOTTLE), FluidConstants.BOTTLE)
		);
	}

	@ParameterizedTest
	@MethodSource("extractCases")
	void testExtract(TestCase c) {
		MutableContainerItemContext ctx = new MutableContainerItemContext(c.input());
		Storage<FluidVariant> storage = FluidStorage.ITEM.find(c.input(), ctx);
		assertNotNull(storage);

		try (Transaction tx = Transaction.openOuter()) {
			long extracted = storage.extract(c.fluid(), c.amount(), tx);
			assertEquals(c.expectedFluid(), extracted);
			if (c.commit()) tx.commit();
		}

		assertEquals(c.expectedItem().getItem(), ctx.getItemVariant().getItem());
	}
}
