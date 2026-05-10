package com.github.salandora.sophisticatedfabriclib.tests.transfer;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.MutableContainerItemContext;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.wrapper.fabric.FabricFluidStorageWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class FabricFluidStorageWrapperTest {
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
	void accessorsExposeSingleTankAndContainerContext() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		ItemStack containerStack = new ItemStack(Items.BUCKET, 3);
		ContainerItemContext context = MutableContainerItemContext.ofSingleStack(containerStack);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage, context);

		assertEquals(1, wrapper.getTanks());
		assertEquals(Long.MAX_VALUE, wrapper.getTankCapacity(0));
		assertTrue(wrapper.isFluidValid(0, new FluidStack(Fluids.WATER, FluidConstants.BUCKET)));
		assertEquals(containerStack.getItem(), wrapper.getContainer().getItem());
		assertEquals(containerStack.getCount(), wrapper.getContainer().getCount());
	}

	@Test
	void getFluidInTankReturnsEmptyWhenStorageHasNoExtractableContent() {
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(fixedStorage(FluidConstants.BUCKET));

		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
	}

	@Test
	void getFluidInTankReturnsStoredExtractableContent() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		insertDirectly(storage, water, FluidConstants.BUCKET / 2);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		FluidStack stored = wrapper.getFluidInTank(0);

		assertEquals(water, stored.getResource());
		assertEquals(FluidConstants.BUCKET / 2, stored.getAmount());
	}

	@Test
	void setFluidInTankIsNoop() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		insertDirectly(storage, water, FluidConstants.BUCKET / 2);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		wrapper.setFluidInTank(0, new FluidStack(Fluids.LAVA, FluidConstants.BUCKET));

		assertEquals(water, wrapper.getFluidInTank(0).getResource());
		assertEquals(FluidConstants.BUCKET / 2, wrapper.getFluidInTank(0).getAmount());
	}

	@Test
	void fillReturnsZeroForEmptyStackWithoutTouchingStorage() {
		AtomicInteger commits = new AtomicInteger();
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET, commits);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		assertEquals(0, wrapper.fill(FluidStack.EMPTY, IFluidHandler.FluidAction.EXECUTE));
		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
		assertEquals(0, commits.get());
	}

	@Test
	void fillSimulatesAndExecutesThroughStorageTransactions() {
		AtomicInteger commits = new AtomicInteger();
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET, commits);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		assertEquals(FluidConstants.BUCKET, wrapper.fill(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), IFluidHandler.FluidAction.SIMULATE));
		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
		assertEquals(0, commits.get());

		assertEquals(FluidConstants.BUCKET, wrapper.fill(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), IFluidHandler.FluidAction.EXECUTE));
		assertEquals(water, wrapper.getFluidInTank(0).getResource());
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());
		assertEquals(1, commits.get());
	}

	@Test
	void fillClampsToCapacityAndRejectsDifferentFluid() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		insertDirectly(storage, water, FluidConstants.BUCKET / 2);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		assertEquals(FluidConstants.BUCKET / 2, wrapper.fill(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), IFluidHandler.FluidAction.EXECUTE));
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());

		assertEquals(0, wrapper.fill(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET), IFluidHandler.FluidAction.EXECUTE));
		assertEquals(water, wrapper.getFluidInTank(0).getResource());
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());
	}

	@Test
	void drainByStackReturnsEmptyForEmptyRequestWithoutTouchingStorage() {
		AtomicInteger commits = new AtomicInteger();
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET, commits);
		insertDirectly(storage, water, FluidConstants.BUCKET);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		FluidStack drained = wrapper.drain(FluidStack.EMPTY, IFluidHandler.FluidAction.EXECUTE);

		assertSame(FluidStack.EMPTY, drained);
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());
		assertEquals(1, commits.get());
	}

	@Test
	void drainByStackSimulatesAndExecutesThroughStorageTransactions() {
		AtomicInteger commits = new AtomicInteger();
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET, commits);
		insertDirectly(storage, water, FluidConstants.BUCKET);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		FluidStack simulated = wrapper.drain(new FluidStack(Fluids.WATER, FluidConstants.BUCKET / 2), IFluidHandler.FluidAction.SIMULATE);
		assertEquals(water, simulated.getResource());
		assertEquals(FluidConstants.BUCKET / 2, simulated.getAmount());
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());

		FluidStack drained = wrapper.drain(new FluidStack(Fluids.WATER, FluidConstants.BUCKET / 2), IFluidHandler.FluidAction.EXECUTE);
		assertEquals(water, drained.getResource());
		assertEquals(FluidConstants.BUCKET / 2, drained.getAmount());
		assertEquals(FluidConstants.BUCKET / 2, wrapper.getFluidInTank(0).getAmount());
		assertEquals(2, commits.get());
	}

	@Test
	void drainByStackRejectsDifferentFluidAndClampsToStoredAmount() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		insertDirectly(storage, water, FluidConstants.BUCKET / 4);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		FluidStack wrongFluid = wrapper.drain(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET), IFluidHandler.FluidAction.EXECUTE);
		assertEquals(lava, wrongFluid.getResource());
		assertEquals(0, wrongFluid.getAmount());
		assertEquals(FluidConstants.BUCKET / 4, wrapper.getFluidInTank(0).getAmount());

		FluidStack drained = wrapper.drain(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), IFluidHandler.FluidAction.EXECUTE);
		assertEquals(water, drained.getResource());
		assertEquals(FluidConstants.BUCKET / 4, drained.getAmount());
		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
	}

	@Test
	void drainByAmountReturnsEmptyWhenNoResourceIsStored() {
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(fixedStorage(FluidConstants.BUCKET));

		assertSame(FluidStack.EMPTY, wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE));
	}

	@Test
	void blankStoredResourceIsTreatedAsEmpty() {
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(new BlankResourceStorage());

		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
		assertSame(FluidStack.EMPTY, wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE));
	}

	@Test
	void drainByAmountUsesStoredResourceAndHonorsSimulateVsExecute() {
		SingleFluidStorage storage = fixedStorage(FluidConstants.BUCKET);
		insertDirectly(storage, water, FluidConstants.BUCKET);
		FabricFluidStorageWrapper wrapper = FabricFluidStorageWrapper.of(storage);

		FluidStack simulated = wrapper.drain(FluidConstants.BUCKET / 2, IFluidHandler.FluidAction.SIMULATE);
		assertEquals(water, simulated.getResource());
		assertEquals(FluidConstants.BUCKET / 2, simulated.getAmount());
		assertEquals(FluidConstants.BUCKET, wrapper.getFluidInTank(0).getAmount());

		FluidStack drained = wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE);
		assertEquals(water, drained.getResource());
		assertEquals(FluidConstants.BUCKET, drained.getAmount());
		assertSame(FluidStack.EMPTY, wrapper.getFluidInTank(0));
	}

	private static SingleFluidStorage fixedStorage(long capacity) {
		return fixedStorage(capacity, new AtomicInteger());
	}

	private static SingleFluidStorage fixedStorage(long capacity, AtomicInteger commits) {
		return SingleFluidStorage.withFixedCapacity(capacity, commits::incrementAndGet);
	}

	private static void insertDirectly(SingleFluidStorage storage, FluidVariant variant, long amount) {
		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(amount, storage.insert(variant, amount, transaction));
			transaction.commit();
		}
	}

	private static class BlankResourceStorage implements Storage<FluidVariant> {
		private final StorageView<FluidVariant> view = new StorageView<>() {
			@Override
			public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
				return maxAmount;
			}

			@Override
			public boolean isResourceBlank() {
				return false;
			}

			@Override
			public FluidVariant getResource() {
				return FluidVariant.blank();
			}

			@Override
			public long getAmount() {
				return FluidConstants.BUCKET;
			}

			@Override
			public long getCapacity() {
				return FluidConstants.BUCKET;
			}
		};

		@Override
		public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			return maxAmount;
		}

		@Override
		public Iterator<StorageView<FluidVariant>> iterator() {
			return List.of(view).iterator();
		}

		@Override
		public Iterator<StorageView<FluidVariant>> nonEmptyIterator() {
			return iterator();
		}
	}
}
