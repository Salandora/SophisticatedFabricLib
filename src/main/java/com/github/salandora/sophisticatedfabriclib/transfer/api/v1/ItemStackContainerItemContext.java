package com.github.salandora.sophisticatedfabriclib.transfer.api.v1;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.impl.transfer.context.SingleSlotContainerItemContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackContainerItemContext extends ContainerItemContext {
	static ContainerItemContext ofSingleStack(ItemStack stack) {
		return new SingleSlotContainerItemContext(new Slot(stack)) {
			@Nullable
			@Override
			public <A> A find(ItemApiLookup<A, ContainerItemContext> lookup) {
				return getItemVariant().isBlank() ? null : lookup.find(((Slot) getMainSlot()).getStack(), this);
			}
		};
	}

	class Slot extends SingleStackStorage {
		private ItemStack stack;

		public Slot(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		protected ItemStack getStack() {
			return this.stack;
		}

		@Override
		protected void setStack(ItemStack stack) {
			this.stack = stack;
		}
	}
}
