package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface SophisticatedMutableDataComponentHolder extends DataComponentHolder {
	<T> @Nullable T sophisticatedFabricLibrary_set(DataComponentType<? super T> type, @Nullable T value);

	default <T> @Nullable T sophisticatedFabricLibrary_set(Supplier<? extends DataComponentType<? super T>> componentType, @Nullable T value) {
		return this.sophisticatedFabricLibrary_set(componentType.get(), value);
	}

	<T> @Nullable T sophisticatedFabricLibrary_remove(DataComponentType<? extends T> type);

	default <T> @Nullable T sophisticatedFabricLibrary_remove(Supplier<? extends DataComponentType<? extends T>> componentType) {
		return this.sophisticatedFabricLibrary_remove(componentType.get());
	}
}
