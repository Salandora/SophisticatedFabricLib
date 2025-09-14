package com.github.salandora.sophisticatedlibrary.registry;

import com.github.salandora.sophisticatedlibrary.util.DeferredHolder;
import com.github.salandora.sophisticatedlibrary.util.DeferredRegister;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DeferredRegistryTest implements ModInitializer {
	static final String MODID = "deferred_registry_test";

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
	private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);
	private static final DeferredRegister<Object> DOESNT_EXIST_REG = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(MODID, "doesnt_exist"), MODID);

	private static final DeferredHolder<Block, Block> BLOCK = BLOCKS.register("test_block", () -> new Block(BlockBehaviour.Properties.of()));
	private static final DeferredHolder<Item, Item> ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties().stacksTo(1)));
	private static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COMPONENT_TYPE = COMPONENTS.register("test_components", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
	private static final DeferredHolder<Object, Object> DOESNT_EXIST = DOESNT_EXIST_REG.register("test_doesnt_exist", Object::new);

	public DeferredRegistryTest() {
		BLOCKS.register();
		ITEMS.register();
		COMPONENTS.register();
	}

	@Override
	public void onInitialize() {
		BLOCK.get();
		ITEM.get();
		COMPONENT_TYPE.get();
		if (DOESNT_EXIST.isBound()) {
			throw new IllegalStateException("DeferredRegistry doesnt_exist should not be bound");
		}
	}
}
