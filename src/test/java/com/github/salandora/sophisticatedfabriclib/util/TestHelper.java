package com.github.salandora.sophisticatedfabriclib.util;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.stream.IntStream;

public class TestHelper {
	public static final FluidVariant WATER = FluidVariant.of(Fluids.WATER);

	public static TestFluidStorage filledStorage(long amount) {
		TestFluidStorage storage = new TestFluidStorage(FluidConstants.BUCKET * 10);
		storage.insertTestFluid(WATER, amount);
		return storage;
	}

	public static TestFluidStorage emptyStorage() {
		return new TestFluidStorage(FluidConstants.BUCKET * 10);
	}

	public static ItemStackHandler emptyItemStorage() {
		return new ItemStackHandler(1);
	}

	public static boolean containsItem(Inventory storage, Item expected) {
		return storage.items.stream()
				.anyMatch(stack -> stack.getItem() == expected);
	}

	public static boolean containsItem(ItemStackHandler storage, ItemStack stack) {
		return IntStream.range(0, storage.getSlotCount())
				.mapToObj(storage::getStackInSlot)
				.anyMatch(slotStack -> ItemStack.isSameItemSameTags(slotStack, stack));
	}
}
