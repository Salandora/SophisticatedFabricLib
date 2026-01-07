package com.github.salandora.sophisticatedfabriclib.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredRegister<T> {
	public static <R> DeferredRegister<R> create(Registry<R> registry, String namespace) {
		return new DeferredRegister<>(registry.key(), namespace);
	}

	public static <R> DeferredRegister<R> create(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
		return new DeferredRegister<>(registryKey, namespace);
	}

	public static <R> DeferredRegister<R> create(ResourceLocation registryLocation, String namespace) {
		return new DeferredRegister<>(ResourceKey.createRegistryKey(registryLocation), namespace);
	}

	private final ResourceKey<? extends Registry<T>> registryKey;
	private final String namespace;

	private final Map<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entries;

	private DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = registryKey;
		this.namespace = namespace;
		this.entries = new LinkedHashMap<>();
	}

	public Collection<DeferredHolder<T, ? extends T>> getEntries() {
		return Collections.unmodifiableSet(entries.keySet());
	}

	public <U extends T> DeferredHolder<T, U> register(String name, Supplier<U> supplier) {
		DeferredHolder<T, U> holder = DeferredHolder.create(this.registryKey, ResourceLocation.fromNamespaceAndPath(this.namespace, name));

		if (entries.putIfAbsent(holder, supplier) != null) {
			throw new IllegalArgumentException("Duplicate entry " + name);
		}

		return holder;
	}

	@SuppressWarnings("unchecked")
	public void register() {
		Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());
		entries.forEach((key, value) -> Registry.register(registry, key.getId(), value.get()));
	}
}
