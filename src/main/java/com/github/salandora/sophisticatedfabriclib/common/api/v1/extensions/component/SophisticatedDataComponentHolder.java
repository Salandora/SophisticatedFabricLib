package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface SophisticatedDataComponentHolder {
	private DataComponentHolder self() {
		return (DataComponentHolder)this;
	}

	default <T> @Nullable T sophisticatedFabricLibrary_get(Supplier<? extends DataComponentType<? extends T>> componentType) {
		return this.self().get(componentType.get());
	}

	default <T> T sophisticatedFabricLibrary_getOrDefault(Supplier<? extends DataComponentType<? extends T>> type, T defaultValue) {
		return this.self().getOrDefault(type.get(), defaultValue);
	}

	default <T extends DataComponentType<?>> boolean sophisticatedFabricLibrary_has(Supplier<T> type) {
		return this.self().has(type.get());
	}
}
