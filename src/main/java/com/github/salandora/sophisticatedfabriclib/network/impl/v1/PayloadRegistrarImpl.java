package com.github.salandora.sophisticatedfabriclib.network.impl.v1;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.network.api.v1.IPayloadContext;
import com.github.salandora.sophisticatedfabriclib.network.api.v1.PayloadRegistrar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

public abstract class PayloadRegistrarImpl implements PayloadRegistrar {
	public static PayloadRegistrar create() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return new Client();
		} else {
			return new Server();
		}
	}

	private PayloadRegistrarImpl() {
	}

	@Override
	public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
		SophisticatedFabricLib.LOGGER.info("Registering C2S packet [{}]", type.id());
		PayloadTypeRegistry.playC2S().register(type, codec);
		ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
			var ctx = createContext(context.player(), context.server(), context.responseSender());
			handler.receive(payload, ctx);
		});
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

	private static class Server extends PayloadRegistrarImpl {
		public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
			PayloadTypeRegistry.playS2C().register(type, codec);
		}
	}

	@Environment(EnvType.CLIENT)
	private static class Client extends PayloadRegistrarImpl {
		@Override
		public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
			SophisticatedFabricLib.LOGGER.info("Registering S2C packet [{}]", type.id());
			PayloadTypeRegistry.playS2C().register(type, codec);
			ClientPlayNetworking.registerGlobalReceiver(type, new ClientPlayPayloadHandler<>(handler));
		}

		record ClientPlayPayloadHandler<T extends CustomPacketPayload>(PayloadHandler<T> handler)
				implements ClientPlayNetworking.PlayPayloadHandler<T> {
			@Override
			public void receive(T payload, ClientPlayNetworking.Context context) {
				var ctx = createContext(context.player(), context.client(), context.responseSender());
				handler.receive(payload, ctx);
			}
		}
	}
}
