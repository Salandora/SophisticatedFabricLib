package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.IntStream;

public class ItemHandlerSnapshotParticipant extends SnapshotParticipant<List<ItemStack>> {
	private IItemHandler handler;

	public ItemHandlerSnapshotParticipant(IItemHandler handler) {
		this.handler = handler;
	}

	@Override
	protected List<ItemStack> createSnapshot() {
		return IntStream
				.range(0, handler.getSlotCount())
				.mapToObj(slot -> handler.getStackInSlot(slot).copy())
				.toList();
	}

	@Override
	protected void readSnapshot(List<ItemStack> snapshot) {
		IntStream
				.range(0, snapshot.size())
				.forEach(slot -> handler.setStackInSlot(slot, snapshot.get(slot)));
	}
}
