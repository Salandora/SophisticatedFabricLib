package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component.SophisticatedMutableDataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_MutableDataComponentHolder implements SophisticatedMutableDataComponentHolder {
	@Shadow
	@Nullable
	public abstract <T> T set(DataComponentType<? super T> component, @Nullable T value);

	@Shadow
	@Nullable
	public abstract <T> T remove(DataComponentType<? extends T> component);

	@Override
	public <T> @Nullable T sophisticatedFabricLibrary_set(DataComponentType<? super T> type, @Nullable T value) {
		return this.set(type, value);
	}

	@Override
	public <T> @Nullable T sophisticatedFabricLibrary_remove(DataComponentType<? extends T> type) {
		return this.remove(type);
	}
}
