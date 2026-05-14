package com.github.salandora.sophisticatedfabriclib.tests.registry;

import com.github.salandora.sophisticatedfabriclib.util.DeferredHolder;
import com.github.salandora.sophisticatedfabriclib.util.DeferredRegister;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DeferredRegistryTest {
	private static final String MODID = "deferred_registry_test";

	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void registersBlockItemAndComponentIntoBuiltInRegistries() {
		DeferredRegister<Block> blocks = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
		DeferredRegister<Item> items = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

		DeferredHolder<Block, Block> block = blocks.register("test_block", () -> new Block(BlockBehaviour.Properties.of()));
		DeferredHolder<Item, Item> item = items.register("test_item", () -> new Item(new Item.Properties().stacksTo(1)));

		assertFalse(block.isBound());
		assertFalse(item.isBound());

		blocks.register();
		items.register();

		assertSame(block.get(), BuiltInRegistries.BLOCK.get(block.getId()));
		assertSame(item.get(), BuiltInRegistries.ITEM.get(item.getId()));
		assertTrue(block.isBound());
		assertTrue(item.isBound());
	}

	@Test
	void tracksEntriesAndRejectsDuplicateNames() {
		DeferredRegister<Item> items = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

		DeferredHolder<Item, Item> first = items.register("duplicate_test_item", () -> new Item(new Item.Properties()));
		Collection<DeferredHolder<Item, ? extends Item>> entries = items.getEntries();

		assertEquals(1, entries.size());
		assertTrue(entries.contains(first));
		assertThrows(IllegalArgumentException.class, () -> items.register("duplicate_test_item", () -> new Item(new Item.Properties())));
	}

	@Test
	void holderForMissingRegistryStaysUnboundAndThrowsOnGet() {
		DeferredRegister<Object> missingRegistry = DeferredRegister.create(new ResourceLocation(MODID, "doesnt_exist"), MODID);
		DeferredHolder<Object, Object> missing = missingRegistry.register("test_doesnt_exist", Object::new);

		assertFalse(missing.isBound());
		assertEquals(new ResourceLocation(MODID, "test_doesnt_exist"), missing.getId());
		assertThrows(IllegalStateException.class, missing::get);
	}
}
