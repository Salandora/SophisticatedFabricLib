package com.github.salandora.sophisticatedfabriclib.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeferredHolder<T, U extends T> implements Holder<T>, Supplier<U> {

	public static <T, U extends T> DeferredHolder<T, U> create(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation valueName) {
		return create(ResourceKey.create(registryKey, valueName));
	}

	public static <T, U extends T> DeferredHolder<T, U> create(ResourceKey<T> registryKey) {
		return new DeferredHolder<>(registryKey);
	}

	private final ResourceKey<T> key;
	@Nullable
	private Holder<T> holder = null;

	private DeferredHolder(ResourceKey<T> key) {
		this.key = key;
		this.bind(false);
	}

	protected void bind(boolean throwOnMissing) {
		if (this.holder != null) {
			return;
		}

		//noinspection unchecked
		Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(this.key.registry());
		if (registry != null) {
			this.holder = registry.getHolder(this.key).orElse(null);
		} else if (throwOnMissing) {
			throw new IllegalStateException("Registry not found DeferredHolder(" + this.key + ") / " + this.key.registry());
		}
	}

	public ResourceLocation getId() {
		return this.key.location();
	}

	public ResourceKey<T> getKey() {
		return this.key;
	}

	@Override
	public U value() {
		this.bind(true);
		if (this.holder == null) {
			throw new NullPointerException("Holder is null DeferredHolder(" + this.key + ")");
		}
		//noinspection unchecked
		return (U) this.holder.value();
	}

	@Override
	public U get() {
		return this.value();
	}

	@Override
	public boolean isBound() {
		this.bind(false);
		return this.holder != null && this.holder.isBound();
	}

	@Override
	public boolean is(ResourceLocation location) {
		return location.equals(this.key.location());
	}

	@Override
	public boolean is(ResourceKey<T> resourceKey) {
		return resourceKey.equals(this.key);
	}

	@Override
	public boolean is(Predicate<ResourceKey<T>> predicate) {
		return predicate.test(this.key);
	}

	@Override
	public boolean is(TagKey<T> tagKey) {
		this.bind(false);
		return this.holder != null && this.holder.is(tagKey);
	}

	@Override
	public Stream<TagKey<T>> tags() {
		this.bind(false);
		return this.holder != null ? this.holder.tags() : Stream.empty();
	}

	@Override
	public Either<ResourceKey<T>, T> unwrap() {
		return Either.left(this.key);
	}

	@Override
	public Optional<ResourceKey<T>> unwrapKey() {
		return Optional.of(this.key);
	}

	@Override
	public Kind kind() {
		return Kind.REFERENCE;
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> owner) {
		this.bind(false);
		return this.holder != null && this.holder.canSerializeIn(owner);
	}
}
