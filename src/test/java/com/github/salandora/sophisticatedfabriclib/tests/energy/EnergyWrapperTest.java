package com.github.salandora.sophisticatedfabriclib.tests.energy;

import com.github.salandora.sophisticatedfabriclib.energy.api.v1.IEnergyStorage;
import com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn.EnergyStorageWrapper;
import com.github.salandora.sophisticatedfabriclib.energy.api.v1.wrapper.teamreborn.IEnergyStorageWrapper;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.MutableContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EnergyWrapperTest {
	@BeforeAll
	static void boot() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	static final class SimpleIEnergyStorage implements IEnergyStorage {
		private int energyStored;
		private final int cap;
		private final boolean canReceive;
		private final boolean canExtract;

		SimpleIEnergyStorage(int stored, int cap) {
			this(stored, cap, true, true);
		}

		SimpleIEnergyStorage(int stored, int cap, boolean canReceive, boolean canExtract) {
			this.energyStored = stored;
			this.cap = cap;
			this.canReceive = canReceive;
			this.canExtract = canExtract;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!canReceive()) {
				return 0;
			}

			int ret = Math.min(getMaxEnergyStored() - energyStored, maxReceive);
			if (!simulate) {
				energyStored += ret;
			}
			return ret;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!canExtract()) {
				return 0;
			}

			int ret = Math.min(energyStored, maxExtract);
			if (!simulate) {
				energyStored -= ret;
			}
			return ret;
		}

		@Override
		public void setEnergyStored(int stored) {
			energyStored = stored;
		}

		@Override
		public int getEnergyStored() {
			return energyStored;
		}

		@Override
		public int getMaxEnergyStored() {
			return cap;
		}

		@Override
		public boolean canExtract() {
			return canExtract;
		}

		@Override
		public boolean canReceive() {
			return canReceive;
		}
	}

	static class SimpleTREnergyStorage extends SimpleEnergyStorage {
		private int insertCalls;
		private int extractCalls;

		SimpleTREnergyStorage(long amount, long capacity) {
			this(amount, capacity, Long.MAX_VALUE, Long.MAX_VALUE);
		}

		SimpleTREnergyStorage(long amount, long capacity, long maxInsert, long maxExtract) {
			super(capacity, maxInsert, maxExtract);
			this.amount = amount;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			insertCalls++;
			return super.insert(maxAmount, transaction);
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			extractCalls++;
			return super.extract(maxAmount, transaction);
		}
	}

	record TestCase(
			long startAmount,
			long capacity,
			long max,
			boolean simulate,
			long expectedMoved,
			long expectedFinalAmount
	) {}

	// --------------------------
	// IEnergyStorage -> EnergyStorage
	// --------------------------
	static Stream<TestCase> receiveCases() {
		return Stream.of(
				// start, cap, max, commit, moved, final
				new TestCase(10, 100, 40, true,  40, 10),
				new TestCase(10, 100, 40, false, 40, 50),

				// clamp to capacity
				new TestCase(90, 100, 50, false, 10, 100)
		);
	}

	@ParameterizedTest
	@MethodSource("receiveCases")
	void testEnergyStorageWrapper_receiveEnergy(TestCase tc) {
		SimpleTREnergyStorage tr = new SimpleTREnergyStorage(tc.startAmount(), tc.capacity());
		IEnergyStorage wrapped = EnergyStorageWrapper.of(tr);

		int received = wrapped.receiveEnergy((int) tc.max(), tc.simulate());

		assertEquals(tc.expectedMoved(), received);
		assertEquals(tc.expectedFinalAmount(), tr.getAmount());
	}

	static Stream<TestCase> extractCases() {
		return Stream.of(
				// start, cap, max, commit, moved, final
				new TestCase(70, 100, 30, true,  30, 70),
				new TestCase(70, 100, 30, false, 30, 40),

				// clamp to stored
				new TestCase(5, 100, 50, false, 5, 0)
		);
	}

	@ParameterizedTest
	@MethodSource("extractCases")
	void testEnergyStorageWrapper_extractEnergy(TestCase tc) {
		SimpleTREnergyStorage tr = new SimpleTREnergyStorage(tc.startAmount(), tc.capacity());
		IEnergyStorage wrapped = EnergyStorageWrapper.of(tr);

		int extracted = wrapped.extractEnergy((int) tc.max(), tc.simulate());

		assertEquals(tc.expectedMoved(), extracted);
		assertEquals(tc.expectedFinalAmount(), tr.getAmount());
	}

	@Test
	void testEnergyStorageWrapper_accessorsAndCapabilitiesDelegateToWrappedStorage() {
		SimpleTREnergyStorage tr = new SimpleTREnergyStorage(25, 100, 0, 15);
		IEnergyStorage wrapped = EnergyStorageWrapper.of(tr);

		assertEquals(25, wrapped.getEnergyStored());
		assertEquals(100, wrapped.getMaxEnergyStored());
		assertFalse(wrapped.canReceive());
		assertTrue(wrapped.canExtract());
	}

	@Test
	void testEnergyStorageWrapper_setEnergyStoredIsNoop() {
		SimpleTREnergyStorage tr = new SimpleTREnergyStorage(25, 100);
		IEnergyStorage wrapped = EnergyStorageWrapper.of(tr);

		wrapped.setEnergyStored(80);

		assertEquals(25, wrapped.getEnergyStored());
		assertEquals(25, tr.getAmount());
	}

	@Test
	void testEnergyStorageWrapper_zeroReceiveAndExtractDoNotTouchWrappedStorage() {
		SimpleTREnergyStorage tr = new SimpleTREnergyStorage(25, 100);
		IEnergyStorage wrapped = EnergyStorageWrapper.of(tr);

		assertEquals(0, wrapped.receiveEnergy(0, false));
		assertEquals(0, wrapped.extractEnergy(0, false));

		assertEquals(0, tr.insertCalls);
		assertEquals(0, tr.extractCalls);
		assertEquals(25, tr.getAmount());
	}

	@Test
	void testEnergyStorageWrapper_getContainerReturnsContextStack() {
		ItemStack stack = new ItemStack(Items.DIAMOND, 3);
		EnergyStorageWrapper wrapped = EnergyStorageWrapper.of(
				new SimpleTREnergyStorage(25, 100),
				MutableContainerItemContext.ofSingleStack(stack)
		);

		ItemStack container = wrapped.getContainer();

		assertNotNull(container);
		assertEquals(Items.DIAMOND, container.getItem());
		assertEquals(3, container.getCount());
	}


	// --------------------------
	// EnergyStorage -> IEnergyStorage
	// --------------------------
	static Stream<TestCase> insertCommitCases() {
		return Stream.of(
				// start, cap, maxInsert, commit, moved, final
				new TestCase(10, 100, 50, false,  50, 60),
				new TestCase(10, 100, 50, true, 50, 10),

				// clamp to capacity
				new TestCase(90, 100, 50, false,  10, 100),
				new TestCase(90, 100, 50, true, 10, 90)
		);
	}

	static Stream<TestCase> extractCommitCases() {
		return Stream.of(
				// start, cap, maxExtract, commit, moved, final
				new TestCase(70, 100, 30, false,  30, 40),
				new TestCase(70, 100, 30, true, 30, 70),

				// clamp to stored
				new TestCase(5, 100, 50, false,  5, 0),
				new TestCase(5, 100, 50, true, 5, 5)
		);
	}

	@ParameterizedTest
	@MethodSource("insertCommitCases")
	void testIEnergyStorageWrapper_insert_commitAndRollback(TestCase tc) {
		SimpleIEnergyStorage ie = new SimpleIEnergyStorage((int) tc.startAmount(), (int) tc.capacity());
		EnergyStorage wrapped = IEnergyStorageWrapper.of(ie);

		try (Transaction tx = Transaction.openOuter()) {
			long inserted = wrapped.insert(tc.max(), tx);
			assertEquals(tc.expectedMoved(), inserted);
			if (!tc.simulate()) tx.commit();
		}

		assertEquals(tc.expectedFinalAmount(), ie.getEnergyStored());
	}

	@ParameterizedTest
	@MethodSource("extractCommitCases")
	void testIEnergyStorageWrapper_extract_commitAndRollback(TestCase tc) {
		SimpleIEnergyStorage ie = new SimpleIEnergyStorage((int) tc.startAmount(), (int) tc.capacity());
		EnergyStorage wrapped = IEnergyStorageWrapper.of(ie);

		try (Transaction tx = Transaction.openOuter()) {
			long extracted = wrapped.extract(tc.max(), tx);
			assertEquals(tc.expectedMoved(), extracted);
			if (!tc.simulate()) tx.commit();
		}

		assertEquals(tc.expectedFinalAmount(), ie.getEnergyStored());
	}

	@Test
	void testIEnergyStorageWrapper_ofNullReturnsNull() {
		assertNull(IEnergyStorageWrapper.of(null));
	}

	@Test
	void testIEnergyStorageWrapper_accessorsAndCapabilitiesDelegateToWrappedStorage() {
		SimpleIEnergyStorage ie = new SimpleIEnergyStorage(25, 100, false, true);
		IEnergyStorageWrapper wrapped = IEnergyStorageWrapper.of(ie);

		assertNotNull(wrapped);
		assertSame(ie, wrapped.getEnergyStorage());
		assertEquals(25, wrapped.getAmount());
		assertEquals(100, wrapped.getCapacity());
		assertFalse(wrapped.supportsInsertion());
		assertTrue(wrapped.supportsExtraction());
	}

	@Test
	void testIEnergyStorageWrapper_insertAndExtractRespectDisabledCapabilities() {
		SimpleIEnergyStorage ie = new SimpleIEnergyStorage(25, 100, false, false);
		EnergyStorage wrapped = IEnergyStorageWrapper.of(ie);

		try (Transaction tx = Transaction.openOuter()) {
			assertEquals(0, wrapped.insert(50, tx));
			assertEquals(0, wrapped.extract(50, tx));
			tx.commit();
		}

		assertEquals(25, ie.getEnergyStored());
	}
}
