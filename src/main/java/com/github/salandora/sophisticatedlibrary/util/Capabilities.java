package com.github.salandora.sophisticatedlibrary.util;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.transfer.IItemHandler;
import com.github.salandora.sophisticatedlibrary.transfer.wrapper.InvWrapper;
import com.github.salandora.sophisticatedlibrary.transfer.wrapper.PlayerInvWrapper;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class Capabilities {
	public static class ItemHandler {
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
		}
	}
}
