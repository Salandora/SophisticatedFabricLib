package com.github.salandora.sophisticatedfabriclib.tests.transfer;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidHandlerWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FabricFluidHandlerWrapperTest {
	private static FluidVariant water;
	private static FluidVariant lava;

	@BeforeAll
	public static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		water = FluidVariant.of(Fluids.WATER);
		lava = FluidVariant.of(Fluids.LAVA);
	}

	@Test
	void ofReturnsNullForNullHandler() {
		assertNull(FabricFluidHandlerWrapper.of(null));
	}

	@Test
	void accessorsAndCapabilitiesDelegateToWrappedHandler() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		assertNotNull(wrapper);
		assertSame(handler, wrapper.getHandler());
		assertTrue(wrapper.supportsInsertion());
		assertTrue(wrapper.supportsExtraction());
	}

	@Test
	void insertCommitsOnlyWhenTransactionCommits() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET, wrapper.insert(water, FluidConstants.BUCKET, transaction));
		}

		assertEquals(0, handler.getFluidInTank(0).getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET, wrapper.insert(water, FluidConstants.BUCKET, transaction));
			transaction.commit();
		}

		assertEquals(water, handler.getFluidInTank(0).getResource());
		assertEquals(FluidConstants.BUCKET, handler.getFluidInTank(0).getAmount());
	}

	@Test
	void insertClampsToAvailableCapacityAndRejectsDifferentFluid() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		handler.setFluidInTank(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET / 2));
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET / 2, wrapper.insert(water, FluidConstants.BUCKET, transaction));
			transaction.commit();
		}

		assertEquals(FluidConstants.BUCKET, handler.getFluidInTank(0).getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, wrapper.insert(lava, FluidConstants.BUCKET, transaction));
			transaction.commit();
		}

		assertEquals(water, handler.getFluidInTank(0).getResource());
		assertEquals(FluidConstants.BUCKET, handler.getFluidInTank(0).getAmount());
	}

	@Test
	void extractCommitsOnlyWhenTransactionCommits() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		handler.setFluidInTank(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET));
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET / 2, wrapper.extract(water, FluidConstants.BUCKET / 2, transaction));
		}

		assertEquals(FluidConstants.BUCKET, handler.getFluidInTank(0).getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET / 2, wrapper.extract(water, FluidConstants.BUCKET / 2, transaction));
			transaction.commit();
		}

		assertEquals(FluidConstants.BUCKET / 2, handler.getFluidInTank(0).getAmount());
	}

	@Test
	void extractRejectsDifferentFluidAndClampsToStoredAmount() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		handler.setFluidInTank(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET / 4));
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(0, wrapper.extract(lava, FluidConstants.BUCKET, transaction));
			assertEquals(FluidConstants.BUCKET / 4, wrapper.extract(water, FluidConstants.BUCKET, transaction));
			transaction.commit();
		}

		assertEquals(0, handler.getFluidInTank(0).getAmount());
	}

	@Test
	void iteratorExposesCachedViewsForEveryTank() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET, FluidConstants.BUCKET * 2);
		handler.setFluidInTank(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET / 2));
		FabricFluidHandlerWrapper wrapper = FabricFluidHandlerWrapper.of(handler);

		List<StorageView<FluidVariant>> firstViews = viewsOf(wrapper);
		List<StorageView<FluidVariant>> secondViews = viewsOf(wrapper);

		assertEquals(2, firstViews.size());
		assertEquals(2, secondViews.size());
		assertTrue(firstViews.containsAll(secondViews));
		assertTrue(secondViews.containsAll(firstViews));

		StorageView<FluidVariant> waterView = firstViews.stream()
				.filter(view -> !view.isResourceBlank())
				.findFirst()
				.orElseThrow();
		StorageView<FluidVariant> emptyView = firstViews.stream()
				.filter(StorageView::isResourceBlank)
				.findFirst()
				.orElseThrow();

		assertEquals(water, waterView.getResource());
		assertEquals(FluidConstants.BUCKET / 2, waterView.getAmount());
		assertEquals(FluidConstants.BUCKET, waterView.getCapacity());
		assertEquals(FluidVariant.blank(), emptyView.getResource());
		assertEquals(0, emptyView.getAmount());
		assertEquals(FluidConstants.BUCKET * 2, emptyView.getCapacity());
	}

	@Test
	void viewExtractionUsesWrapperTransactionSnapshot() {
		TestFluidHandler handler = new TestFluidHandler(FluidConstants.BUCKET);
		handler.setFluidInTank(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET));
		StorageView<FluidVariant> view = FabricFluidHandlerWrapper.of(handler).iterator().next();

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET / 2, view.extract(water, FluidConstants.BUCKET / 2, transaction));
		}

		assertEquals(FluidConstants.BUCKET, handler.getFluidInTank(0).getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(FluidConstants.BUCKET / 2, view.extract(water, FluidConstants.BUCKET / 2, transaction));
			transaction.commit();
		}

		assertEquals(FluidConstants.BUCKET / 2, handler.getFluidInTank(0).getAmount());
	}

	private static List<StorageView<FluidVariant>> viewsOf(FabricFluidHandlerWrapper wrapper) {
		List<StorageView<FluidVariant>> views = new ArrayList<>();
		Iterator<StorageView<FluidVariant>> iterator = wrapper.iterator();
		while (iterator.hasNext()) {
			views.add(iterator.next());
		}
		return views;
	}

	private static class TestFluidHandler implements IFluidHandler {
		private final long[] capacities;
		private final FluidStack[] fluids;

		private TestFluidHandler(long... capacities) {
			this.capacities = capacities;
			this.fluids = new FluidStack[capacities.length];
			for (int i = 0; i < this.fluids.length; i++) {
				this.fluids[i] = FluidStack.EMPTY;
			}
		}

		@Override
		public int getTanks() {
			return fluids.length;
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			return fluids[tank];
		}

		@Override
		public void setFluidInTank(int tank, FluidStack fluidStack) {
			fluids[tank] = fluidStack.copy();
		}

		@Override
		public long getTankCapacity(int tank) {
			return capacities[tank];
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack fluidStack) {
			return true;
		}

		@Override
		public long fill(FluidStack fluidStack, FluidAction action) {
			if (fluidStack.isEmpty()) {
				return 0;
			}

			for (int tank = 0; tank < fluids.length; tank++) {
				FluidStack stored = fluids[tank];
				if (!stored.isEmpty() && !FluidStack.isSameFluidSameComponents(stored, fluidStack)) {
					continue;
				}

				long inserted = Math.min(fluidStack.getAmount(), capacities[tank] - stored.getAmount());
				if (inserted <= 0) {
					continue;
				}

				if (action.execute()) {
					if (stored.isEmpty()) {
						fluids[tank] = fluidStack.copyWithAmount(inserted);
					} else {
						stored.grow(inserted);
					}
				}
				return inserted;
			}

			return 0;
		}

		@Override
		public FluidStack drain(FluidStack drainStack, FluidAction action) {
			if (drainStack.isEmpty()) {
				return FluidStack.EMPTY;
			}

			for (int tank = 0; tank < fluids.length; tank++) {
				FluidStack stored = fluids[tank];
				if (!stored.isEmpty() && FluidStack.isSameFluidSameComponents(stored, drainStack)) {
					long extracted = Math.min(drainStack.getAmount(), stored.getAmount());
					if (action.execute()) {
						stored.shrink(extracted);
					}
					return drainStack.copyWithAmount(extracted);
				}
			}

			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(long maxDrain, FluidAction action) {
			for (FluidStack fluid : fluids) {
				if (!fluid.isEmpty()) {
					return drain(fluid.copyWithAmount(maxDrain), action);
				}
			}
			return FluidStack.EMPTY;
		}
	}
}
