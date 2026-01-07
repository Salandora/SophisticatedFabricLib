package com.github.salandora.sophisticatedfabriclib.network.api.v0;

import com.github.salandora.sophisticatedfabriclib.network.impl.v0.SimpleChannelImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface NetworkRegistry {
	static SimpleChannel newSimpleChannel(ResourceLocation channelName, Supplier<String> protocolSupplier, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
		return SimpleChannelImpl.create(channelName);
	}
}
