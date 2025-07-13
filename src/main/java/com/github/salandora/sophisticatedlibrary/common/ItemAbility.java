package com.github.salandora.sophisticatedlibrary.common;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record ItemAbility(String name) {
	private static final Map<String, ItemAbility> actions = new ConcurrentHashMap<>();

	public static ItemAbility get(String name) {
		return actions.computeIfAbsent(name, ItemAbility::new);
	}

	@Override
	public @NotNull String toString() {
		return "ItemAbility[%s]".formatted(name);
	}
}
