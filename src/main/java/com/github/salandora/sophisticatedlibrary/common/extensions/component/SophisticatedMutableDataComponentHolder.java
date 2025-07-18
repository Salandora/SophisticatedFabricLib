package com.github.salandora.sophisticatedlibrary.common.extensions.component;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface SophisticatedMutableDataComponentHolder extends DataComponentHolder {
	<T> @Nullable T sophisticatedLibrary_set(DataComponentType<? super T> type, @Nullable T value);

	default <T> @Nullable T sophisticatedLibrary_set(Supplier<? extends DataComponentType<? super T>> componentType, @Nullable T value) {
		return this.sophisticatedLibrary_set(componentType.get(), value);
	}

	<T> @Nullable T sophisticatedLibrary_remove(DataComponentType<? extends T> type);

	default <T> @Nullable T sophisticatedLibrary_remove(Supplier<? extends DataComponentType<? extends T>> componentType) {
		return this.sophisticatedLibrary_remove(componentType.get());
	}
}
