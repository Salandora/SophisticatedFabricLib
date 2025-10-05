package com.github.salandora.sophisticatedlibrary.network.api.v1;

import com.github.salandora.sophisticatedlibrary.network.impl.v1.PayloadRegistrarImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface PayloadRegistrar {
	static PayloadRegistrar registrar() {
		return PayloadRegistrarImpl.create();
	}

	<T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler);
	<T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler);

	@FunctionalInterface
	interface PayloadHandler<T extends CustomPacketPayload> {
		void receive(T payload, IPayloadContext context);
	}
}
