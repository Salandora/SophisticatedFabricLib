package com.github.salandora.sophisticatedlibrary.common.api.v1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record ItemAbility(String name) {
	private static final Map<String, ItemAbility> actions = new ConcurrentHashMap<>();

	public static ItemAbility get(String name) {
		return actions.computeIfAbsent(name, ItemAbility::new);
	}

	@Override
	public String toString() {
		return "ItemAbility[%s]".formatted(name);
	}
}

