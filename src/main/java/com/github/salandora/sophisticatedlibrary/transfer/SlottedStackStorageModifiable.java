package com.github.salandora.sophisticatedlibrary.transfer;

import net.minecraft.world.item.ItemStack;

public interface SlottedStackStorageModifiable extends SlottedStackStorage {
	void setStackInSlot(int slot, ItemStack stack);
}
