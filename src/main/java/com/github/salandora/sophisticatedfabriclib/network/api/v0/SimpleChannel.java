package com.github.salandora.sophisticatedfabriclib.network.api.v0;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface SimpleChannel {
	<M> void registerMessage(int index, Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer);

	<M> void sendToServer(M message);

	<M> void sendToClient(ServerPlayer player, M message);

	default void initServerListener() {}

	default void initClientListener() {}
}
