package com.github.salandora.sophisticatedfabriclib.tests.transfer;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.*;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InventoryWrapperTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void invWrapperInsertsExtractsAndHonorsSimulation() {
		SimpleContainer container = new SimpleContainer(2);
		InvWrapper wrapper = InvWrapper.of(container);
		ItemStack input = new ItemStack(Items.DIRT, 10);

		assertTrue(wrapper.insertItem(0, input, true).isEmpty());
		assertTrue(container.getItem(0).isEmpty());

		assertTrue(wrapper.insertItem(0, input, false).isEmpty());
		assertStack(container.getItem(0), Items.DIRT, 10);

		ItemStack remainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 60), false);
		assertStack(container.getItem(0), Items.DIRT, 64);
		assertStack(remainder, Items.DIRT, 6);

		ItemStack simulatedExtraction = wrapper.extractItem(0, 5, true);
		assertStack(simulatedExtraction, Items.DIRT, 5);
		assertStack(container.getItem(0), Items.DIRT, 64);

		ItemStack extracted = wrapper.extractItem(0, 64, false);
		assertStack(extracted, Items.DIRT, 64);
		assertTrue(container.getItem(0).isEmpty());

		ItemStack stone = new ItemStack(Items.STONE, 3);
		wrapper.setStackInSlot(1, stone);
		assertSame(stone, wrapper.getStackInSlot(1));
		assertEquals(2, wrapper.getSlotCount());
		assertEquals(container.getMaxStackSize(), wrapper.getSlotLimit(1));
		assertTrue(wrapper.isItemValid(1, new ItemStack(Items.DIRT)));
	}

	@Test
	void invWrapperRejectsInvalidItemsLimitsStacksAndUsesContainerIdentityForEquality() {
		RestrictedContainer container = new RestrictedContainer(1);
		InvWrapper wrapper = InvWrapper.of(container);
		ItemStack stone = new ItemStack(Items.STONE, 3);

		assertSame(stone, wrapper.insertItem(0, stone, false));
		assertTrue(container.getItem(0).isEmpty());
		assertEquals(0, container.changedCount);
		assertFalse(wrapper.isItemValid(0, stone));

		ItemStack remainder = wrapper.insertItem(0, new ItemStack(Items.DIRT, 6), false);
		assertStack(container.getItem(0), Items.DIRT, 4);
		assertStack(remainder, Items.DIRT, 2);
		assertEquals(2, container.changedCount);

		InvWrapper sameContainer = InvWrapper.of(container);
		assertEquals(wrapper, sameContainer);
		assertEquals(wrapper.hashCode(), sameContainer.hashCode());
		assertNotEquals(wrapper, InvWrapper.of(new RestrictedContainer(1)));
	}

	@Test
	void rangedWrapperOffsetsCallsAndRejectsOutOfRangeSlots() {
		ItemStackHandler backing = new ItemStackHandler(5);
		RangedWrapper wrapper = new RangedWrapper(backing, 1, 4);
		ItemStack dirt = new ItemStack(Items.DIRT, 7);

		assertEquals(3, wrapper.getSlotCount());
		wrapper.setStackInSlot(0, dirt);
		assertSame(dirt, backing.getStackInSlot(1));
		assertSame(dirt, wrapper.getStackInSlot(0));

		assertTrue(wrapper.insertItem(1, new ItemStack(Items.STONE, 2), false).isEmpty());
		assertStack(backing.getStackInSlot(2), Items.STONE, 2);

		ItemStack extracted = wrapper.extractItem(0, 3, false);
		assertStack(extracted, Items.DIRT, 3);
		assertStack(backing.getStackInSlot(1), Items.DIRT, 4);
		assertEquals(Item.ABSOLUTE_MAX_STACK_SIZE, wrapper.getSlotLimit(2));
		assertTrue(wrapper.isItemValid(2, new ItemStack(Items.DIRT)));

		ItemStack invalidInput = new ItemStack(Items.DIRT, 1);
		wrapper.setStackInSlot(3, new ItemStack(Items.DIAMOND, 1));
		assertTrue(wrapper.getStackInSlot(3).isEmpty());
		assertSame(invalidInput, wrapper.insertItem(3, invalidInput, false));
		assertTrue(wrapper.extractItem(3, 1, false).isEmpty());
		assertEquals(0, wrapper.getSlotLimit(3));
		assertFalse(wrapper.isItemValid(3, new ItemStack(Items.DIRT)));
		assertTrue(backing.getStackInSlot(4).isEmpty());
	}

	@Test
	void combinedInvWrapperRoutesSlotsAcrossHandlersAndRejectsOutOfRangeSlots() {
		ItemStackHandler first = new ItemStackHandler(2);
		ItemStackHandler second = new ItemStackHandler(1);
		CombinedInvWrapper combined = new CombinedInvWrapper(first, second);
		ItemStack dirt = new ItemStack(Items.DIRT, 4);
		ItemStack diamond = new ItemStack(Items.DIAMOND, 1);

		assertEquals(3, combined.getSlotCount());
		combined.setStackInSlot(0, dirt);
		assertSame(dirt, first.getStackInSlot(0));

		combined.setStackInSlot(2, diamond);
		assertSame(diamond, second.getStackInSlot(0));
		assertSame(diamond, combined.getStackInSlot(2));

		assertTrue(combined.insertItem(1, new ItemStack(Items.STONE, 2), false).isEmpty());
		assertStack(first.getStackInSlot(1), Items.STONE, 2);

		ItemStack extracted = combined.extractItem(2, 1, false);
		assertStack(extracted, Items.DIAMOND, 1);
		assertTrue(second.getStackInSlot(0).isEmpty());
		assertEquals(Item.ABSOLUTE_MAX_STACK_SIZE, combined.getSlotLimit(1));
		assertTrue(combined.isItemValid(1, new ItemStack(Items.DIRT)));

		ItemStack invalidInput = new ItemStack(Items.DIRT, 1);
		assertTrue(combined.getStackInSlot(3).isEmpty());
		assertSame(invalidInput, combined.insertItem(3, invalidInput, false));
		assertTrue(combined.extractItem(3, 1, false).isEmpty());
		assertEquals(0, combined.getSlotLimit(3));
		assertFalse(combined.isItemValid(3, new ItemStack(Items.DIRT)));
	}

	@Test
	void playerMainInvWrapperTargetsMainInventory() {
		Player player = mockPlayer();
		Inventory inventory = player.getInventory();
		PlayerMainInvWrapper wrapper = PlayerMainInvWrapper.of(player);

		assertSame(inventory, wrapper.getInventoryPlayer());
		assertEquals(inventory.items.size(), wrapper.getSlotCount());
		assertTrue(wrapper.insertItem(0, new ItemStack(Items.DIRT, 8), false).isEmpty());
		assertStack(inventory.items.get(0), Items.DIRT, 8);

		assertTrue(wrapper.insertItem(1, new ItemStack(Items.STONE, 3), true).isEmpty());
		assertTrue(inventory.items.get(1).isEmpty());

		PlayerMainInvWrapper fromInventory = PlayerMainInvWrapper.of(inventory);
		assertEquals(inventory.items.size(), fromInventory.getSlotCount());
	}

	@Test
	void playerArmorInvWrapperOnlyAcceptsMatchingArmorSlot() {
		Player player = mockPlayer();
		Inventory inventory = player.getInventory();
		PlayerArmorInvWrapper wrapper = PlayerArmorInvWrapper.of(player);
		ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
		ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
		int feetSlot = EquipmentSlot.FEET.getIndex();

		assertSame(inventory, wrapper.getInventoryPlayer());
		assertEquals(inventory.armor.size(), wrapper.getSlotCount());
		assertTrue(wrapper.insertItem(feetSlot, boots, false).isEmpty());
		assertStack(inventory.armor.get(feetSlot), Items.DIAMOND_BOOTS, 1);

		assertSame(helmet, wrapper.insertItem(feetSlot, helmet, false));
		assertStack(inventory.armor.get(feetSlot), Items.DIAMOND_BOOTS, 1);

		ItemStack dirt = new ItemStack(Items.DIRT);
		assertSame(dirt, wrapper.insertItem(EquipmentSlot.HEAD.getIndex(), dirt, false));
		assertTrue(inventory.armor.get(EquipmentSlot.HEAD.getIndex()).isEmpty());

		PlayerArmorInvWrapper fromInventory = PlayerArmorInvWrapper.of(inventory);
		assertEquals(inventory.armor.size(), fromInventory.getSlotCount());
	}

	@Test
	void playerOffhandInvWrapperTargetsOffhandInventory() {
		Player player = mockPlayer();
		Inventory inventory = player.getInventory();
		PlayerOffhandInvWrapper wrapper = PlayerOffhandInvWrapper.of(player);

		assertEquals(inventory.offhand.size(), wrapper.getSlotCount());
		assertTrue(wrapper.insertItem(0, new ItemStack(Items.SHIELD), false).isEmpty());
		assertStack(inventory.offhand.get(0), Items.SHIELD, 1);

		ItemStack simulatedExtraction = wrapper.extractItem(0, 1, true);
		assertStack(simulatedExtraction, Items.SHIELD, 1);
		assertStack(inventory.offhand.get(0), Items.SHIELD, 1);

		ItemStack extracted = wrapper.extractItem(0, 1, false);
		assertStack(extracted, Items.SHIELD, 1);
		assertTrue(inventory.offhand.get(0).isEmpty());

		PlayerOffhandInvWrapper fromInventory = PlayerOffhandInvWrapper.of(inventory);
		assertEquals(inventory.offhand.size(), fromInventory.getSlotCount());
	}

	@Test
	void playerInvWrapperCombinesMainArmorAndOffhandWrappers() {
		Player player = mockPlayer();
		Inventory inventory = player.getInventory();
		PlayerInvWrapper wrapper = PlayerInvWrapper.of(player);
		int armorBaseSlot = inventory.items.size();
		int offhandSlot = armorBaseSlot + inventory.armor.size();

		assertEquals(inventory.items.size() + inventory.armor.size() + inventory.offhand.size(), wrapper.getSlotCount());

		assertTrue(wrapper.insertItem(0, new ItemStack(Items.DIRT, 2), false).isEmpty());
		assertStack(inventory.items.get(0), Items.DIRT, 2);

		assertTrue(wrapper.insertItem(armorBaseSlot + EquipmentSlot.FEET.getIndex(), new ItemStack(Items.DIAMOND_BOOTS), false).isEmpty());
		assertStack(inventory.armor.get(EquipmentSlot.FEET.getIndex()), Items.DIAMOND_BOOTS, 1);

		assertTrue(wrapper.insertItem(offhandSlot, new ItemStack(Items.SHIELD), false).isEmpty());
		assertStack(inventory.offhand.get(0), Items.SHIELD, 1);

		PlayerInvWrapper fromInventory = PlayerInvWrapper.of(inventory);
		assertEquals(wrapper.getSlotCount(), fromInventory.getSlotCount());
	}

	private static Player mockPlayer() {
		Player player = mock(Player.class);
		Inventory inventory = new Inventory(player);
		Level level = mock(Level.class);

		when(player.getInventory()).thenReturn(inventory);
		when(player.level()).thenReturn(level);
		when(player.getEquipmentSlotForItem(org.mockito.ArgumentMatchers.any(ItemStack.class))).thenAnswer(invocation -> {
			ItemStack stack = invocation.getArgument(0);
			if (stack.is(Items.DIAMOND_BOOTS)) {
				return EquipmentSlot.FEET;
			}
			if (stack.is(Items.DIAMOND_HELMET)) {
				return EquipmentSlot.HEAD;
			}
			return EquipmentSlot.MAINHAND;
		});
		return player;
	}

	private static void assertStack(ItemStack stack, Item item, int count) {
		assertEquals(item, stack.getItem());
		assertEquals(count, stack.getCount());
	}

	private static final class RestrictedContainer extends SimpleContainer {
		private int changedCount;

		private RestrictedContainer(int size) {
			super(size);
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			return stack.is(Items.DIRT);
		}

		@Override
		public int getMaxStackSize() {
			return 4;
		}

		@Override
		public void setChanged() {
			changedCount++;
			super.setChanged();
		}
	}
}
