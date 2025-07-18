package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
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

	private static class Slot extends SingleStackStorage {
		private ItemStack stack;

		public Slot(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		protected ItemStack getStack() {
			return stack;
		}

		@Override
		protected void setStack(ItemStack stack) {
			this.stack = stack;
		}
	}
}
