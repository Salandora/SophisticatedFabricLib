package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.component.SophisticatedMutableDataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_MutableDataComponentHolder implements SophisticatedMutableDataComponentHolder {
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
