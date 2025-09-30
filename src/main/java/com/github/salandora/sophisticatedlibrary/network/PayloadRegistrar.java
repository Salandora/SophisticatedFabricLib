package com.github.salandora.sophisticatedlibrary.network;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.network.handling.IPayloadContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

public interface PayloadRegistrar {
	static PayloadRegistrar registrar() {
		return new PayloadRegistrar() {
			@Override
			public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
				SophisticatedLibrary.LOGGER.info("Registering C2S packet [{}]", type.id());
				PayloadTypeRegistry.playC2S().register(type, codec);
				ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
					var ctx = createContext(context.player(), context.server(), context.responseSender());
					handler.receive(payload, ctx);
				});
			}

			@Override
			@Environment(EnvType.CLIENT)
			public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
				SophisticatedLibrary.LOGGER.info("Registering S2C packet [{}]", type.id());
				PayloadTypeRegistry.playS2C().register(type, codec);
				ClientPlayNetworking.registerGlobalReceiver(type, new ClientPlayPayloadHandler<>(handler));
			}
		};
	}

	private static IPayloadContext createContext(Player player, BlockableEventLoop<?> queue, PacketSender responseSender) {
		return new IPayloadContext() {
			@Override
			public Player player() {
				return player;
			}

			@Override
			public void enqueueWork(Runnable runnable) {
				queue.execute(runnable);
			}

			@Override
			public PacketSender responseSender() {
				return responseSender;
			}
		};
	}

	<T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler);
	default <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
		PayloadTypeRegistry.playS2C().register(type, codec);
	}

	@FunctionalInterface
	interface PayloadHandler<T extends CustomPacketPayload> {
		void receive(T payload, IPayloadContext context);
	}

	// Need this record cause lambdas won't work
	@Environment(EnvType.CLIENT)
	record ClientPlayPayloadHandler<T extends CustomPacketPayload>(PayloadHandler<T> handler)
			implements ClientPlayNetworking.PlayPayloadHandler<T> {
		@Override
		public void receive(T payload, ClientPlayNetworking.Context context) {
			var ctx = createContext(context.player(), context.client(), context.responseSender());
			handler.receive(payload, ctx);
		}
	}
}
