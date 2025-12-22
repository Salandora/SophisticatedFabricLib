package com.github.salandora.sophisticatedlibrary.util;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedlibrary.fluid.api.v1.IFluidHandlerItem;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.InvWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.PlayerInvWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric.FabricFluidHandlerWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric.FabricFluidStorageWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric.FabricItemStorageWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.items.ShulkerInvWrapper;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.List;

public class Capabilities {
	public static class ItemHandler {
		/**
		 * Can be used to register Item ItemHandlers
		 */
		public static final ItemApiLookup<IItemHandler, ContainerItemContext> ITEM = ItemApiLookup.get(SophisticatedLibrary.id("item_item_storage"), IItemHandler.class, ContainerItemContext.class);

		/**
		 * Can be used to get an IItemHandler directly or through fallback to {@link ItemStorage#SIDED} with a wrapper
		 */
		public static final BlockApiLookup<IItemHandler, Direction> SIDED = BlockApiLookup.get(SophisticatedLibrary.id("sided_block_storage"), IItemHandler.class, Direction.class);

		/**
		 * Capability for the inventory of an entity.
		 * If an entity has multiple inventory "subparts", this capability should give a combined view of all the subparts.
		 */
		public static final EntityApiLookup<IItemHandler, Void> ENTITY = EntityApiLookup.get(SophisticatedLibrary.id("entity_item_storage"), IItemHandler.class, Void.class);

		/**
		 * Capability for an inventory of entity that should be accessible to automation,
		 * in the sense that droppers, hoppers, and similar modded devices will try to use it.
		 */
		public static final EntityApiLookup<IItemHandler, Direction> ENTITY_AUTOMATION = EntityApiLookup.get(SophisticatedLibrary.id("entity_automation_item_storage"), IItemHandler.class, Direction.class);

		static {
			var containerEntities = List.of(
					EntityType.CHEST_BOAT,
					EntityType.CHEST_MINECART,
					EntityType.HOPPER_MINECART);
			for (var entityType : containerEntities) {
				ENTITY.registerForType((entity, ctx) -> InvWrapper.of(entity), entityType);
				ENTITY_AUTOMATION.registerForType((inventory, direction) -> InvWrapper.of(inventory), entityType);
			}

			ENTITY.registerForType((player, ctx) -> PlayerInvWrapper.of(player), EntityType.PLAYER);

			ITEM.registerForItems((stack, ctx) -> new ShulkerInvWrapper(stack),
					Items.SHULKER_BOX,
					Items.WHITE_SHULKER_BOX,
					Items.ORANGE_SHULKER_BOX,
					Items.MAGENTA_SHULKER_BOX,
					Items.LIGHT_BLUE_SHULKER_BOX,
					Items.YELLOW_SHULKER_BOX,
					Items.LIME_SHULKER_BOX,
					Items.PINK_SHULKER_BOX,
					Items.GRAY_SHULKER_BOX,
					Items.LIGHT_GRAY_SHULKER_BOX,
					Items.CYAN_SHULKER_BOX,
					Items.PURPLE_SHULKER_BOX,
					Items.BLUE_SHULKER_BOX,
					Items.BROWN_SHULKER_BOX,
					Items.GREEN_SHULKER_BOX,
					Items.RED_SHULKER_BOX,
					Items.BLACK_SHULKER_BOX
			);

			SIDED.registerFallback((level, pos, state,be, dir) -> {
				var storage = ItemStorage.SIDED.find(level, pos, state, be, dir);
				if (storage == null) {
					return null;
				}

				if (storage instanceof FabricItemHandlerWrapper wrapper) {
					return wrapper.getHandler();
				}

				return FabricItemStorageWrapper.of(storage);
			});
		}
	}

	public static class FluidHandler {
		/**
		 * Can be used to register FluidHandlers directly or through a fallback to {@link net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage#ITEM} with a wrapper
		 */
		public static final ItemApiLookup<IFluidHandlerItem, ContainerItemContext> ITEM = ItemApiLookup.get(SophisticatedLibrary.id("item_fluid_storage"), IFluidHandlerItem.class, ContainerItemContext.class);

		/**
		 * Can be used to get an IItemHandler directly or through fallback to {@link net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage#SIDED} with a wrapper
		 */
		public static final BlockApiLookup<IFluidHandler, Direction> SIDED = BlockApiLookup.get(SophisticatedLibrary.id("block_fluid_storage"), IFluidHandler.class, Direction.class);

		static {
			ITEM.registerFallback((stack, ctx) -> {
				var storage = FluidStorage.ITEM.find(stack, ctx);
				if (storage == null) {
					return null;
				}

				if (storage instanceof FabricFluidHandlerWrapper wrapper) {
					return (IFluidHandlerItem) wrapper.getHandler();
				}

				return FabricFluidStorageWrapper.of(storage, ctx);
			});

			SIDED.registerFallback((level, pos, state, be, dir) -> {
				var storage = FluidStorage.SIDED.find(level, pos, state, be, dir);
				if (storage == null) {
					return null;
				}

				if (storage instanceof FabricFluidHandlerWrapper wrapper) {
					return wrapper.getHandler();
				}

				return FabricFluidStorageWrapper.of(storage);
			});
		}
	}
}
