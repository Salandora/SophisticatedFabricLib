package com.github.salandora.sophisticatedfabriclib.transfer.impl.v1.wrapper.fabric;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidHandlerWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FabricFluidHandlerWrapperImpl extends SnapshotParticipant<List<FluidStack>> implements FabricFluidHandlerWrapper {
	public static FabricFluidHandlerWrapperImpl of(IFluidHandler handler) {
		if (handler == null) {
			return null;
		}

		return new FabricFluidHandlerWrapperImpl(handler);
	}

	private final IFluidHandler handler;
	private Set<StorageView<FluidVariant>> views;

	private FabricFluidHandlerWrapperImpl(IFluidHandler handler) {
		this.handler = handler;
	}

	@Override
	public IFluidHandler getHandler() {
		return this.handler;
	}

	@Override
	public boolean supportsInsertion() {
		return FabricFluidHandlerWrapper.super.supportsInsertion();
	}

	@Override
	public long insert(FluidVariant variant, long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});

		return handler.fill(new FluidStack(variant.getFluid(), maxAmount), IFluidHandler.FluidAction.EXECUTE);
	}

	@Override
	public boolean supportsExtraction() {
		return FabricFluidHandlerWrapper.super.supportsExtraction();
	}

	@Override
	public long extract(FluidVariant variant, long maxAmount, TransactionContext ctx) {
		updateSnapshots(ctx);
		ctx.addCloseCallback((c, r) -> {
			if (r.wasCommitted()) {
				//handler.update();
			}
		});

		return handler.drain(new FluidStack(variant.getFluid(), maxAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		if (this.views != null) {
			return views.iterator();
		}

		this.views = IntStream.range(0, this.handler.getTanks())
				.<StorageView<FluidVariant>>mapToObj(WrapperSlotView::new)
				.collect(Collectors.toSet());

		return views.iterator();
	}

	@Override
	protected List<FluidStack> createSnapshot() {
		return IntStream.range(0, this.handler.getTanks()).mapToObj(slot -> this.handler.getFluidInTank(slot).copy()).toList();
	}

	@Override
	protected void readSnapshot(List<FluidStack> snapshot) {
		IntStream.range(0, snapshot.size()).forEach(slot -> this.handler.setFluidInTank(slot, snapshot.get(slot)));
	}

	private final class WrapperSlotView implements StorageView<FluidVariant> {
		private final int slot;

		private WrapperSlotView(int slot) {
			this.slot = slot;
		}

		private FluidStack getStack() {
			return handler.getFluidInTank(slot);
		}

		@Override
		public long extract(FluidVariant variant, long maxAmount, TransactionContext ctx) {
			return FabricFluidHandlerWrapperImpl.this.extract(variant, maxAmount, ctx);
		}

		@Override
		public boolean isResourceBlank() {
			return getStack().isEmpty();
		}

		@Override
		public FluidVariant getResource() {
			return getStack().getVariant();
		}

		@Override
		public long getAmount() {
			return getStack().getAmount();
		}

		@Override
		public long getCapacity() {
			return handler.getTankCapacity(slot);
		}
	}
}
