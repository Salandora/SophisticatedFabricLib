package com.github.salandora.sophisticatedlibrary.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedlibrary.transfer.api.v1.IItemHandler;
import com.github.salandora.sophisticatedlibrary.transfer.api.v1.wrapper.fabric.FabricItemHandlerWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class FabricItemHandlerWrapperImpl extends SnapshotParticipant<List<ItemStack>> implements FabricItemHandlerWrapper {
	public static FabricItemHandlerWrapper of(IItemHandler handler) {
		if (handler == null) {
			return null;
		}

		return new FabricItemHandlerWrapperImpl(handler);
	}

	private final IItemHandler handler;

	private FabricItemHandlerWrapperImpl(IItemHandler handler) {
		this.handler = handler;
	}

	public IItemHandler getHandler() {
		return this.handler;
	}

	@Override
	public int getSlotCount() {
		return this.handler.getSlotCount();
	}

	@Override
	public boolean supportsInsertion() {
		return FabricItemHandlerWrapper.super.supportsInsertion();
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});

		return handler.insert(resource.toStack((int) maxAmount), false);
	}

	@Override
	public boolean supportsExtraction() {
		return FabricItemHandlerWrapper.super.supportsExtraction();
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});

		return handler.extract(resource.toStack((int) maxAmount), false);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return IntStream.range(0, this.handler.getSlotCount())
				.<StorageView<ItemVariant>>mapToObj(WrapperSlotView::new)
				.iterator();
	}

	@Override
	protected List<ItemStack> createSnapshot() {
		return IntStream.range(0, this.handler.getSlotCount()).mapToObj(slot -> this.handler.getStackInSlot(slot).copy()).toList();
	}

	@Override
	protected void readSnapshot(List<ItemStack> snapshot) {
		IntStream.range(0, snapshot.size()).forEach(slot -> this.handler.setStackInSlot(slot, snapshot.get(slot)));
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return new WrapperStackStorage(slot);
	}

	private final class WrapperSlotView implements StorageView<ItemVariant> {
		private final int slot;

		private WrapperSlotView(int slot) {
			this.slot = slot;
		}

		private ItemStack getStack() {
			return handler.getStackInSlot(slot);
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext ctx) {
			updateSnapshots(ctx);
			ctx.addCloseCallback((c, r) -> {
				if (r.wasCommitted()) {
					//handler.update();
				}
			});

			return handler.extractItem(slot, (int) maxAmount, false).getCount();
		}

		@Override
		public boolean isResourceBlank() {
			return getStack().isEmpty();
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.of(getStack());
		}

		@Override
		public long getAmount() {
			return getStack().getCount();
		}

		@Override
		public long getCapacity() {
			return handler.getSlotLimit(slot);
		}
	}

	private final class WrapperStackStorage extends SingleStackStorage {
		private final int slot;

		public WrapperStackStorage(int slot) {
			this.slot = slot;
		}

		@Override
		protected ItemStack getStack() {
			return handler.getStackInSlot(slot);
		}

		@Override
		protected void setStack(ItemStack stack) {
			handler.setStackInSlot(slot, stack);
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			//handler.update();
		}
	}
}
