package com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FabricItemHandlerWrapperImpl extends CombinedSlottedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements FabricItemHandlerWrapper {
	private static final Map<IItemHandler, FabricItemHandlerWrapperImpl> WRAPPERS = (new MapMaker()).weakValues().makeMap();

	public static FabricItemHandlerWrapper of(IItemHandler handler) {
		if (handler == null) {
			return null;
		}

		FabricItemHandlerWrapperImpl storage = WRAPPERS.computeIfAbsent(handler, FabricItemHandlerWrapperImpl::new);
		storage.resizeSlotList();
		return storage;
	}

	private final IItemHandler handler;
	final List<WrapperStackStorage> backingList;

	private FabricItemHandlerWrapperImpl(IItemHandler handler) {
		super(Collections.emptyList());
		this.handler = handler;
		this.backingList = new ArrayList<>();
	}

	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return this.parts;
	}

	private void resizeSlotList() {
		int inventorySize = this.handler.getSlotCount();
		if (inventorySize != this.parts.size()) {
			while(this.backingList.size() < inventorySize) {
				this.backingList.add(new WrapperStackStorage(this, this.backingList.size()));
			}

			this.parts = Collections.unmodifiableList(this.backingList.subList(0, inventorySize));
		}

	}

	public IItemHandler getHandler() {
		return this.handler;
	}

	@Override
	public int getSlotCount() {
		return this.handler.getSlotCount();
	}

	private static final class WrapperStackStorage extends SingleStackStorage {
		private final FabricItemHandlerWrapperImpl storage;
		private final int slot;

		public WrapperStackStorage(FabricItemHandlerWrapperImpl storage, int slot) {
			this.storage = storage;
			this.slot = slot;
		}

		@Override
		protected ItemStack getStack() {
			return storage.handler.getStackInSlot(slot);
		}

		@Override
		protected void setStack(ItemStack stack) {
			storage.handler.setStackInSlot(slot, stack);
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext ctx) {
			if (!canInsert(slot, ((ItemVariantImpl) resource).getCachedStack())) {
				return 0;
			}

			updateSnapshots(ctx);

			ItemStack remaining = storage.handler.insertItem(slot, resource.toStack((int) maxAmount), false);
			return (maxAmount - remaining.getCount());
		}

		private boolean canInsert(int slot, ItemStack stack) {
			return storage.handler.isItemValid(slot, stack);
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext ctx) {
			ItemStack stack = getStack();
			if (stack.isEmpty() || !resource.matches(stack)) {
				return 0;
			}

			updateSnapshots(ctx);
			return storage.handler.extractItem(slot, (int) maxAmount, false).getCount();
		}

		@Override
		public int getCapacity(ItemVariant variant) {
			return Math.min(storage.handler.getSlotLimit(slot), super.getCapacity(variant));
		}

		@Override
		protected ItemStack createSnapshot() {
			// Compression upgrade does not override the stack in setStackInSlot
			// this means we can not setStack the copy but instead need to use
			// the copy as the snapshot
			ItemStack original = getStack();
			return original.copy();
		}
	}
}
