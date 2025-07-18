package com.github.salandora.sophisticatedlibrary.common.mixin;

import com.github.salandora.sophisticatedlibrary.common.extensions.component.SophisticatedMutableDataComponentHolder;
import com.github.salandora.sophisticatedlibrary.common.extensions.item.SophisticatedItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements SophisticatedItemStack, SophisticatedMutableDataComponentHolder {
	@Shadow
	@Final
	PatchedDataComponentMap components;

	@Override
	public <T> @Nullable T sophisticatedLibrary_set(DataComponentType<? super T> type, @Nullable T value) {
		return this.components.set(type, value);
	}

	@Override
	public <T> @Nullable T get(DataComponentType<? extends T> type) {
		return this.components.get(type);
	}

	@Override
	public <T> @Nullable T sophisticatedLibrary_remove(DataComponentType<? extends T> type) {
		return this.components.remove(type);
	}
}
