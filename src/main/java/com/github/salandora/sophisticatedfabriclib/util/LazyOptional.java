package com.github.salandora.sophisticatedfabriclib.util;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazyOptional<T> {
	private static final @NotNull LazyOptional<Void> EMPTY = new LazyOptional<>(null);
	private static final Logger LOGGER = LoggerFactory.getLogger(LazyOptional.class);

	private final Supplier<T> supplier;
	private final Object lock = new Object();
	private volatile Mutable<T> resolved;
	private Set<Consumer<LazyOptional<T>>> listeners = new HashSet<>();
	private boolean isValid = true;

	public static <T> LazyOptional<T> of(final @Nullable Supplier<T> instanceSupplier) {
		return instanceSupplier == null ? empty() : new LazyOptional<>(instanceSupplier);
	}

	public static <T> LazyOptional<T> empty() {
		return EMPTY.cast();
	}

	@SuppressWarnings("unchecked")
	public <X> LazyOptional<X> cast() {
		return (LazyOptional<X>) this;
	}

	private LazyOptional(@Nullable Supplier<T> instanceSupplier) {
		this.supplier = instanceSupplier;
	}

	private @Nullable T getValue() {
		if (!isValid || supplier == null) {
			return null;
		}

		if (resolved == null) {
			synchronized (lock) {
				if (resolved == null) {
					T temp = supplier.get();
					if (temp == null) {
						LOGGER.error("Supplier should not return null value");
					}
					resolved = new MutableObject<>(temp);
				}
			}
		}
		return resolved.getValue();
	}

	public boolean isPresent() {
		return supplier != null && isValid;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		Objects.requireNonNull(consumer);
		T val = getValue();
		if (isValid && val != null) {
			consumer.accept(val);
		}
	}

	public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return isPresent() && getValue() != null ? Optional.of(mapper.apply(getValue())) : Optional.empty();
	}

	public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
		return resolve().flatMap(mapper);
	}

	public Optional<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		final T value = getValue(); // To keep the non-null contract we have to evaluate right now. Should we allow this function at all?
		return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
	}

	public Optional<T> resolve() {
		return isPresent() ? Optional.of(getValue()) : Optional.empty();
	}

	public T orElse(T other) {
		T val = getValue();
		return val != null ? val : other;
	}

	public T orElseGet(Supplier<? extends T> other) {
		T val = getValue();
		return val != null ? val : other.get();
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		T val = getValue();
		if (val != null) {
			return val;
		}
		throw exceptionSupplier.get();
	}

	public void addListener(Consumer<LazyOptional<T>> listener) {
		if (isPresent()) {
			this.listeners.add(listener);
		} else {
			listener.accept(this);
		}
	}

	public void invalidate() {
		if (this.isValid) {
			this.isValid = false;
			this.listeners.forEach(e -> e.accept(this));
		}
	}
}