package com.github.salandora.sophisticatedfabriclib.tests.registry;

import com.github.salandora.sophisticatedfabriclib.util.DeferredHolder;
import com.github.salandora.sophisticatedfabriclib.util.DeferredRegister;
import com.mojang.serialization.Codec;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponentType;
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
		DeferredRegister<DataComponentType<?>> components = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);

		DeferredHolder<Block, Block> block = blocks.register("test_block", () -> new Block(BlockBehaviour.Properties.of()));
		DeferredHolder<Item, Item> item = items.register("test_item", () -> new Item(new Item.Properties().stacksTo(1)));
		DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> componentType = components.register(
				"test_components",
				() -> DataComponentType.<Integer>builder().persistent(Codec.INT).build()
		);

		assertFalse(block.isBound());
		assertFalse(item.isBound());
		assertFalse(componentType.isBound());

		blocks.register();
		items.register();
		components.register();

		assertSame(block.get(), BuiltInRegistries.BLOCK.get(block.getId()));
		assertSame(item.get(), BuiltInRegistries.ITEM.get(item.getId()));
		assertSame(componentType.get(), BuiltInRegistries.DATA_COMPONENT_TYPE.get(componentType.getId()));
		assertTrue(block.isBound());
		assertTrue(item.isBound());
		assertTrue(componentType.isBound());
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
		DeferredRegister<Object> missingRegistry = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(MODID, "doesnt_exist"), MODID);
		DeferredHolder<Object, Object> missing = missingRegistry.register("test_doesnt_exist", Object::new);

		assertFalse(missing.isBound());
		assertEquals(ResourceLocation.fromNamespaceAndPath(MODID, "test_doesnt_exist"), missing.getId());
		assertThrows(IllegalStateException.class, missing::get);
	}
}
