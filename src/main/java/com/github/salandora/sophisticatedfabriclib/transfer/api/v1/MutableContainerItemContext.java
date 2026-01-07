package com.github.salandora.sophisticatedfabriclib.transfer.api.v1;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class MutableContainerItemContext implements ContainerItemContext {
	private final Slot slot;

	public MutableContainerItemContext(ItemStack stack) {
		this.slot = new Slot(stack);
	}

	@Nullable
	@Override
	public <A> A find(ItemApiLookup<A, ContainerItemContext> lookup) {
		return getItemVariant().isBlank() ? null : lookup.find(this.slot.getStack(), this);
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return this.slot;
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		return 0;
	}

	@Override
	public @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return Collections.emptyList();
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
			return this.stack;
		}

		@Override
		protected void setStack(ItemStack stack) {
			this.stack = stack;
		}
	}
}
