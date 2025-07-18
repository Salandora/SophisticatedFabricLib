package com.github.salandora.sophisticatedlibrary.util;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}

	private final Supplier<T> supplier;
	@Nullable
	private volatile T cachedValue;

	private Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		T ret = cachedValue;
		if (ret == null) {
			synchronized (this) {
				ret = cachedValue;
				if (ret == null) {
					cachedValue = ret = supplier.get();
					if (ret == null) {
						throw new IllegalStateException("Lazy value cannot be null, but supplier returned null: " + supplier);
					}
				}
			}
		}
		return ret;
	}
}
