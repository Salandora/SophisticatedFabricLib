package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.impl.transfer.context.SingleSlotContainerItemContext;
import net.minecraft.world.item.ItemStack;

public class MutableContainerItemContext extends SingleSlotContainerItemContext {
	public MutableContainerItemContext(ItemStack stack) {
		super(new Slot(stack));
	}

	@Override
	public String toString() {
		return "MutableContainerItemContext[%d %s %s]".formatted(getMainSlot().getAmount(), getMainSlot().getResource(), getMainSlot());
	}

	private static class Slot extends SingleItemStorage {
		public Slot(ItemStack stack) {
			this.variant = ItemVariant.of(stack);
			this.amount = stack.getCount();
		}

		@Override
		protected long getCapacity(ItemVariant variant) {
			return variant.getItem().getDefaultMaxStackSize();
		}
	}
}
