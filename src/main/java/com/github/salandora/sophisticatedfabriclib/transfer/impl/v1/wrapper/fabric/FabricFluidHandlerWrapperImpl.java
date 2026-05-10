package com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidHandlerWrapper;
import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FabricFluidHandlerWrapperImpl extends CombinedStorage<FluidVariant, SingleSlotStorage<FluidVariant>> implements FabricFluidHandlerWrapper {
	private static final Map<IFluidHandler, FabricFluidHandlerWrapperImpl> WRAPPERS = (new MapMaker()).weakValues().makeMap();

	public static FabricFluidHandlerWrapperImpl of(IFluidHandler handler) {
		if (handler == null) {
			return null;
		}

		FabricFluidHandlerWrapperImpl storage = WRAPPERS.computeIfAbsent(handler, FabricFluidHandlerWrapperImpl::new);
		storage.resizeSlotList();
		return storage;
	}

	private final IFluidHandler handler;
	final List<WrapperFluidStorage> backingList;

	private FabricFluidHandlerWrapperImpl(IFluidHandler handler) {
		super(Collections.emptyList());
		this.handler = handler;
		this.backingList = new ArrayList<>();
	}

	public List<SingleSlotStorage<FluidVariant>> getSlots() {
		return this.parts;
	}
	private void resizeSlotList() {
		int inventorySize = this.handler.getTanks();
		if (inventorySize != this.parts.size()) {
			while(this.backingList.size() < inventorySize) {
				this.backingList.add(new WrapperFluidStorage(this, this.backingList.size()));
			}

			this.parts = Collections.unmodifiableList(this.backingList.subList(0, inventorySize));
		}

	}

	@Override
	public IFluidHandler getHandler() {
		return this.handler;
	}

	private static final class WrapperFluidStorage extends SnapshotParticipant<FluidStack> implements SingleSlotStorage<FluidVariant> {
		private final FabricFluidHandlerWrapperImpl storage;
		private final int slot;

		public WrapperFluidStorage(FabricFluidHandlerWrapperImpl storage, int slot) {
			this.storage = storage;
			this.slot = slot;
		}

		private FluidStack getStack() {
			return storage.handler.getFluidInTank(slot);
		}

		private void setStack(FluidStack stack) {
			storage.handler.setFluidInTank(slot, stack);
		}

		@Override
		public long getCapacity() {
			return storage.handler.getTankCapacity(slot);
		}

		@Override
		public long insert(FluidVariant variant, long maxAmount, TransactionContext ctx) {
			if (!canInsert(slot, new FluidStack(variant, maxAmount))) {
				return 0;
			}

			updateSnapshots(ctx);
			return storage.handler.fill(new FluidStack(variant.getFluid(), maxAmount), IFluidHandler.FluidAction.EXECUTE);
		}

		private boolean canInsert(int slot, FluidStack stack) {
			return storage.handler.isFluidValid(slot, stack);
		}

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext ctx) {
			FluidStack stack = getStack();
			if (stack.isResourceBlank() || !resource.equals(stack.getResource())) {
				return 0;
			}

			updateSnapshots(ctx);
			return storage.handler.drain(new FluidStack(resource.getFluid(), maxAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();
		}

		@Override
		public boolean isResourceBlank() {
			return getStack().isResourceBlank();
		}

		@Override
		public FluidVariant getResource() {
			return getStack().getResource();
		}

		@Override
		public long getAmount() {
			return getStack().getAmount();
		}

		@Override
		protected FluidStack createSnapshot() {
			FluidStack original = getStack();
			setStack(original.copy());
			return original;
		}

		@Override
		protected void readSnapshot(FluidStack snapshot) {
			setStack(snapshot);
		}

		@Override
		public String toString() {
			return "WrapperFluidStorage[" + getStack() + "]";
		}
	}
}
