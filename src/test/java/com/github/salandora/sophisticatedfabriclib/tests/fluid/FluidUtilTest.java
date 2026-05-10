package com.github.salandora.sophisticatedfabriclib.tests.fluid;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidActionResult;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidUtil;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedfabriclib.tests.util.TestFluidStorage;
import com.github.salandora.sophisticatedfabriclib.tests.util.TestHelper;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FluidUtilTest {
	@BeforeAll
	public static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	public record TestCase(ItemStack input, ItemStack expectedItem, long expectedFluid, boolean commit) {}
	public record TestCaseStow(ItemStack input, ItemStack expectedItem, ItemStack expectedInvItem, long expectedFluid, boolean commit) {}

	public static ItemStack createItemStack(Item item) {
		return createItemStack(item, 1);
	}
	public static ItemStack createItemStack(Item item, int count) {
		ItemStack itemStack = new ItemStack(item, count);
		itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
		return itemStack;
	}

	private static void assertItemStack(ItemStack expected, ItemStack got) {
		if (got.getItem() != expected.getItem())
			throw new AssertionError("Expected item " + expected.getItem() + ", got: " + got.getItem());

		if (got.getCount() != expected.getCount())
			throw new AssertionError("Expected count " + expected.getCount() + ", got: " + got.getCount());
	}

	@Test
	void testToBucketsConvertsFabricDropletsToMilliBuckets() {
		assertEquals(1000, FluidUtil.toBuckets(FluidConstants.BUCKET));
		assertEquals(250, FluidUtil.toBuckets(FluidConstants.BUCKET / 4));
	}

	@Test
	void testIsFluidStorage() {
		assertTrue(FluidUtil.isFluidStorage(new ItemStack(Items.BUCKET)));
		assertFalse(FluidUtil.isFluidStorage(new ItemStack(Items.DIRT)));
	}

	@Test
	void testTryFluidTransferByAmountSimulateAndExecute() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		TestFluidStorage destination = TestHelper.emptyStorage();

		FluidStack simulated = FluidUtil.tryFluidTransfer(destination, source, FluidConstants.BUCKET, false);
		assertEquals(FluidConstants.BUCKET, simulated.getAmount());
		assertEquals(FluidConstants.BUCKET, source.getAmount());
		assertEquals(0, destination.getAmount());

		FluidStack transferred = FluidUtil.tryFluidTransfer(destination, source, FluidConstants.BUCKET, true);
		assertEquals(FluidConstants.BUCKET, transferred.getAmount());
		assertEquals(0, source.getAmount());
		assertEquals(FluidConstants.BUCKET, destination.getAmount());
	}

	@Test
	void testTryFluidTransferByResourceRequiresMatchingFluidAndSpace() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		TestFluidStorage destination = TestHelper.emptyStorage();

		FluidStack wrongFluid = FluidUtil.tryFluidTransfer(destination, source, new FluidStack(Fluids.LAVA, FluidConstants.BUCKET), true);
		assertTrue(wrongFluid.isEmpty());
		assertEquals(FluidConstants.BUCKET, source.getAmount());
		assertEquals(0, destination.getAmount());

		TestFluidStorage fullDestination = TestHelper.filledStorage(10 * FluidConstants.BUCKET);
		FluidStack noSpace = FluidUtil.tryFluidTransfer(fullDestination, source, new FluidStack(Fluids.WATER, FluidConstants.BUCKET), true);
		assertTrue(noSpace.isEmpty());
		assertEquals(FluidConstants.BUCKET, source.getAmount());
		assertEquals(10 * FluidConstants.BUCKET, fullDestination.getAmount());
	}

	static Stream<TestCase> fillCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.BUCKET), new ItemStack(Items.WATER_BUCKET, 1), 4 * FluidConstants.BUCKET, true),
				new TestCase(new ItemStack(Items.BUCKET), new ItemStack(Items.WATER_BUCKET, 1), 5 * FluidConstants.BUCKET, false),
				new TestCase(new ItemStack(Items.BUCKET, 2), new ItemStack(Items.WATER_BUCKET, 1), 4 * FluidConstants.BUCKET, true),
				new TestCase(new ItemStack(Items.BUCKET, 2), new ItemStack(Items.WATER_BUCKET, 1), 5 * FluidConstants.BUCKET, false),

				new TestCase(new ItemStack(Items.GLASS_BOTTLE), createItemStack(Items.POTION), 5 * FluidConstants.BUCKET - FluidConstants.BOTTLE, true),
				new TestCase(new ItemStack(Items.GLASS_BOTTLE), createItemStack(Items.POTION), 5 * FluidConstants.BUCKET, false),
				new TestCase(new ItemStack(Items.GLASS_BOTTLE, 2), createItemStack(Items.POTION), 5 * FluidConstants.BUCKET - FluidConstants.BOTTLE, true),
				new TestCase(new ItemStack(Items.GLASS_BOTTLE, 2), createItemStack(Items.POTION), 5 * FluidConstants.BUCKET, false)
		);
	}

	@ParameterizedTest
	@MethodSource("fillCases")
	void testTryFillContainer(TestCase test) {
		TestFluidStorage source = TestHelper.filledStorage(5 * FluidConstants.BUCKET);
		ItemStack original = test.input().copy();

		FluidActionResult result = FluidUtil.tryFillContainer(test.input(), source, FluidConstants.BUCKET, null, test.commit());

		assertTrue(result.success());
		assertItemStack(original, test.input());
		assertItemStack(test.expectedItem(), result.getResult());
		assertEquals(test.expectedFluid(), source.getAmount());
	}

	@Test
	void testTryFillContainerFailsForNonFluidContainer() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		ItemStack dirt = new ItemStack(Items.DIRT);

		FluidActionResult result = FluidUtil.tryFillContainer(dirt, source, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertItemStack(new ItemStack(Items.DIRT), dirt);
		assertEquals(FluidConstants.BUCKET, source.getAmount());
	}

	@Test
	void testTryFillContainerFailsWhenSourceIsEmpty() {
		TestFluidStorage source = TestHelper.emptyStorage();

		FluidActionResult result = FluidUtil.tryFillContainer(new ItemStack(Items.BUCKET), source, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertEquals(0, source.getAmount());
	}

	static Stream<TestCase> emptyCases() {
		return Stream.of(
				new TestCase(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET, 1), FluidConstants.BUCKET, true),
				new TestCase(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET, 1), 0, false),

				new TestCase(new ItemStack(Items.WATER_BUCKET, 2), new ItemStack(Items.BUCKET, 1), FluidConstants.BUCKET, true),
				new TestCase(new ItemStack(Items.WATER_BUCKET, 2), new ItemStack(Items.BUCKET, 1), 0, false),

				new TestCase(createItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), FluidConstants.BOTTLE, true),
				new TestCase(createItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), 0, false),
				new TestCase(createItemStack(Items.POTION, 2), new ItemStack(Items.GLASS_BOTTLE), FluidConstants.BOTTLE, true),
				new TestCase(createItemStack(Items.POTION, 2), new ItemStack(Items.GLASS_BOTTLE), 0, false)
		);
	}

	@ParameterizedTest
	@MethodSource("emptyCases")
	void testTryEmptyContainer(TestCase test) {
		TestFluidStorage dest = TestHelper.emptyStorage();
		ItemStack original = test.input().copy();

		FluidActionResult result = FluidUtil.tryEmptyContainer(test.input(), dest, FluidConstants.BUCKET, null, test.commit());

		assertTrue(result.success());
		assertItemStack(original, test.input());
		assertItemStack(test.expectedItem(), result.getResult());
		assertEquals(test.expectedFluid(), dest.getAmount());
	}

	@Test
	void testTryEmptyContainerFailsWhenDestinationCannotAcceptFluid() {
		TestFluidStorage dest = TestHelper.filledStorage(10 * FluidConstants.BUCKET);
		ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);

		FluidActionResult result = FluidUtil.tryEmptyContainer(waterBucket, dest, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertItemStack(new ItemStack(Items.WATER_BUCKET), waterBucket);
		assertEquals(10 * FluidConstants.BUCKET, dest.getAmount());
	}

	@Test
	void testTryEmptyContainerFailsWhenContainerIsEmpty() {
		TestFluidStorage dest = TestHelper.emptyStorage();

		FluidActionResult result = FluidUtil.tryEmptyContainer(new ItemStack(Items.BUCKET), dest, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertEquals(0, dest.getAmount());
	}

	static Stream<TestCaseStow> fillStowCases() {
		return Stream.of(
				new TestCaseStow(new ItemStack(Items.BUCKET), new ItemStack(Items.WATER_BUCKET, 1), ItemStack.EMPTY,4 * FluidConstants.BUCKET, true),
				new TestCaseStow(new ItemStack(Items.BUCKET, 2), new ItemStack(Items.BUCKET, 1), new ItemStack(Items.WATER_BUCKET, 1), 4 * FluidConstants.BUCKET, true),
				new TestCaseStow(new ItemStack(Items.BUCKET, 2), new ItemStack(Items.BUCKET, 1), ItemStack.EMPTY, 5 * FluidConstants.BUCKET, false),

				new TestCaseStow(new ItemStack(Items.GLASS_BOTTLE), createItemStack(Items.POTION), ItemStack.EMPTY, 5 * FluidConstants.BUCKET - FluidConstants.BOTTLE, true),
				new TestCaseStow(new ItemStack(Items.GLASS_BOTTLE), createItemStack(Items.POTION), ItemStack.EMPTY, 5 * FluidConstants.BUCKET, false),
				new TestCaseStow(new ItemStack(Items.GLASS_BOTTLE, 2), new ItemStack(Items.GLASS_BOTTLE), createItemStack(Items.POTION), 5 * FluidConstants.BUCKET - FluidConstants.BOTTLE, true),
				new TestCaseStow(new ItemStack(Items.GLASS_BOTTLE, 2), new ItemStack(Items.GLASS_BOTTLE), ItemStack.EMPTY, 5 * FluidConstants.BUCKET, false)
		);
	}

	@ParameterizedTest
	@MethodSource("fillStowCases")
	void testTryFillContainerAndStow(TestCaseStow test) {
		TestFluidStorage source = TestHelper.filledStorage(5 * FluidConstants.BUCKET);
		ItemStackHandler overflowInv = TestHelper.emptyItemStorage();

		ItemStack original = test.input().copy();

		FluidActionResult result = FluidUtil.tryFillContainerAndStow(test.input(), source, overflowInv, FluidConstants.BUCKET, null, test.commit());

		assertTrue(result.success());
		assertItemStack(original, test.input());
		assertItemStack(test.expectedItem(), result.getResult());
		assertTrue(TestHelper.containsItem(overflowInv, test.expectedInvItem()));
		assertEquals(test.expectedFluid(), source.getAmount());
	}

	@Test
	void testTryFillContainerAndStowFailsForEmptyContainerStack() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		ItemStackHandler overflowInv = TestHelper.emptyItemStorage();

		FluidActionResult result = FluidUtil.tryFillContainerAndStow(ItemStack.EMPTY, source, overflowInv, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertEquals(FluidConstants.BUCKET, source.getAmount());
	}

	@Test
	void testTryFillContainerAndStowFailsForStackedContainerWhenInventoryCannotAcceptResult() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		ItemStackHandler fullInventory = new ItemStackHandler(0);
		ItemStack buckets = new ItemStack(Items.BUCKET, 2);

		FluidActionResult result = FluidUtil.tryFillContainerAndStow(buckets, source, fullInventory, FluidConstants.BUCKET, null, true);

		assertFalse(result.success());
		assertItemStack(new ItemStack(Items.BUCKET, 2), buckets);
		assertEquals(FluidConstants.BUCKET, source.getAmount());
	}

	@Test
	void testTryFillContainerAndStowCreativePlayerKeepsOriginalStackResult() {
		TestFluidStorage source = TestHelper.filledStorage(FluidConstants.BUCKET);
		ItemStackHandler fullInventory = new ItemStackHandler(0);
		ItemStack buckets = new ItemStack(Items.BUCKET, 2);
		Player player = mockPlayer();
		player.getAbilities().instabuild = true;

		FluidActionResult result = FluidUtil.tryFillContainerAndStow(buckets, source, fullInventory, FluidConstants.BUCKET, player, true);

		assertTrue(result.success());
		assertItemStack(new ItemStack(Items.BUCKET, 2), result.getResult());
		assertItemStack(new ItemStack(Items.BUCKET, 2), buckets);
		assertEquals(0, source.getAmount());
	}

	static Stream<TestCaseStow> emptyStowCases() {
		return Stream.of(
				new TestCaseStow(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET, 1), ItemStack.EMPTY, FluidConstants.BUCKET, true),
				new TestCaseStow(new ItemStack(Items.WATER_BUCKET, 2), new ItemStack(Items.WATER_BUCKET, 1), new ItemStack(Items.BUCKET, 1), FluidConstants.BUCKET, true),
				new TestCaseStow(new ItemStack(Items.WATER_BUCKET, 2), new ItemStack(Items.WATER_BUCKET, 1), ItemStack.EMPTY, 0, false),

				new TestCaseStow(createItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), ItemStack.EMPTY, FluidConstants.BOTTLE, true),
				new TestCaseStow(createItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), ItemStack.EMPTY, 0, false),
				new TestCaseStow(createItemStack(Items.POTION, 2), createItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), FluidConstants.BOTTLE, true),
				new TestCaseStow(createItemStack(Items.POTION, 2), createItemStack(Items.POTION), ItemStack.EMPTY, 0, false)
		);
	}

	@ParameterizedTest
	@MethodSource("emptyStowCases")
	void testTryEmptyContainerAndStow(TestCaseStow test) {
		TestFluidStorage dest = TestHelper.emptyStorage();
		ItemStackHandler overflowInv = TestHelper.emptyItemStorage();

		ItemStack original = test.input().copy();

		FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(test.input(), dest, overflowInv, FluidConstants.BUCKET, null, test.commit());

		assertTrue(result.success());
		assertItemStack(original, test.input());
		assertItemStack(test.expectedItem(), result.getResult());
		assertTrue(TestHelper.containsItem(overflowInv, test.expectedInvItem()));
		assertEquals(test.expectedFluid(), dest.getAmount());
	}


	public record OfferDropTestCase(String name, DropTestOp op, IFluidHandler storage, ItemStack container, Item expectedResultItem) {}

	@FunctionalInterface
	interface DropTestOp {
		FluidActionResult apply(ItemStack container, IFluidHandler storage, IItemHandler inventory, long maxTransfer, Player player, boolean doTransfer);
	}

	static Stream<OfferDropTestCase> dropTestCases() {
		return Stream.of(
				new OfferDropTestCase("fill", FluidUtil::tryFillContainerAndStow, TestHelper.filledStorage(FluidConstants.BUCKET), new ItemStack(Items.BUCKET, 2), Items.WATER_BUCKET),
				new OfferDropTestCase("empty", FluidUtil::tryEmptyContainerAndStow, TestHelper.emptyStorage(), new ItemStack(Items.WATER_BUCKET, 2), Items.BUCKET)
		);
	}

	private Player mockPlayer() {
		Player player = mock(Player.class);
		Inventory mcInventory = new Inventory(player);
		when(player.getInventory()).thenReturn(mcInventory);

		Abilities abilities = mock(Abilities.class);
		abilities.instabuild = false;
		when(player.getAbilities()).thenReturn(abilities);

		Level level = mock(Level.class);
		when(level.isClientSide()).thenReturn(false);
		when(player.level()).thenReturn(level);

		RandomSource random = RandomSource.create(0xDEADBEEFL);
		when(player.getRandom()).thenReturn(random);

		return player;
	}

	@ParameterizedTest
	@MethodSource("dropTestCases")
	public void testOfferToPlayer(OfferDropTestCase testCase) {
		Player player = mockPlayer();

		// Simulate full inventory
		ItemStackHandler inventory = new ItemStackHandler(0);

		FluidActionResult result = testCase.op.apply(testCase.container.copy(), testCase.storage, inventory, FluidConstants.BUCKET, player, true);

		assertTrue(result.success(), "Transfer should succeed");
		assertTrue(TestHelper.containsItem(player.getInventory(), testCase.expectedResultItem()), "Mock player should receive dropped item: " + testCase.expectedResultItem());

	}

	@ParameterizedTest
	@MethodSource("dropTestCases")
	public void testDropToPlayer(OfferDropTestCase testCase) {
		Player player = mockPlayer();

		// Fill player's inventory completely to simulate no space
		player.getInventory().items.replaceAll(ignored -> new ItemStack(Items.DIRT));

		// Simulate full inventory
		ItemStackHandler inventory = new ItemStackHandler(0);

		FluidActionResult result = testCase.op.apply(testCase.container.copy(), testCase.storage, inventory, FluidConstants.BUCKET, player, true);

		assertTrue(result.success(), "Transfer should succeed");
		verify(player, atLeastOnce()).drop(
				argThat(stack -> stack.getItem() == testCase.expectedResultItem()),
				anyBoolean(),
				anyBoolean()
		);
	}
}
