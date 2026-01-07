package com.github.salandora.sophisticatedfabriclib.network.impl.v0;

import com.github.salandora.sophisticatedfabriclib.network.api.v0.NetworkEvent;
import com.github.salandora.sophisticatedfabriclib.network.api.v0.SimpleChannel;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannelImpl implements SimpleChannel {
	public static SimpleChannelImpl create(ResourceLocation channelName) {
		return new SimpleChannelImpl(channelName);
	}

	protected ResourceLocation channelName;
	private Short2ObjectMap<PacketInfo<?>> packetIndexed = new Short2ObjectOpenHashMap<>();
	private Object2ObjectMap<Class<?>, PacketInfo<?>> packetMap = new Object2ObjectOpenHashMap<>();

	public SimpleChannelImpl(ResourceLocation channelName) {
		this.channelName = channelName;
	}

	@Override
	public <M> void registerMessage(int index, Class<M> clazz, BiConsumer<M, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> handler) {
		PacketInfo<?> info = new PacketInfo<>(index, encoder, decoder, handler);
		packetIndexed.put((short)index, info);
		packetMap.put(clazz, info);
	}

	@Override
	public <M> void sendToServer(M message) {
		FriendlyByteBuf buf = toBuffer(message);
		if (buf == null) {
			return;
		}
		ClientPlayNetworking.send(this.channelName, buf);
	}

	@Override
	public <M> void sendToClient(ServerPlayer player, M message) {
		FriendlyByteBuf buf = toBuffer(message);
		if (buf == null) {
			return;
		}
		ServerPlayNetworking.send(player, this.channelName, buf);
	}

	private void decodeAndRun(FriendlyByteBuf buf, Supplier<NetworkEvent.Context> context) {
		short id = buf.readUnsignedByte();
		final PacketInfo<?> info = packetIndexed.get(id);
		if (info == null) {
			// TODO: Add meaningful message
			return;
		}

		decodeAndRun(buf, context, info);
	}

	private <M> void decodeAndRun(FriendlyByteBuf buf, Supplier<NetworkEvent.Context> context, PacketInfo<M> packetInfo) {
		Optional.ofNullable(packetInfo.decoder)
				.map(d -> d.apply(buf))
				.ifPresent(p -> packetInfo.handler.accept(p, context));
	}

	private static <M> void encode(FriendlyByteBuf buf, M message, PacketInfo<M> packetInfo) {
		buf.writeByte(packetInfo.index & 0xFF);
		packetInfo.encoder.accept(message, buf);
	}

	protected <M> FriendlyByteBuf toBuffer(M message) {
		PacketInfo<M> info = (PacketInfo<M>) packetMap.get(message.getClass());
		if (info == null) {
			// TODO: Add meaningful message
			throw new IllegalArgumentException("Invalid message "+message.getClass().getName());
		}

		FriendlyByteBuf buf = PacketByteBufs.create();
		SimpleChannelImpl.encode(buf, message, info);
		return buf;
	}

	@Override
	public void initServerListener() {
		ServerPlayNetworking.registerGlobalReceiver(channelName, new Client2Server());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void initClientListener() {
		ClientPlayNetworking.registerGlobalReceiver(channelName, new Server2Client());
	}

	private class Client2Server implements ServerPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
			decodeAndRun(buf, () -> new NetworkEvent.Context(server, player));
		}
	}

	@Environment(EnvType.CLIENT)
	private class Server2Client implements ClientPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
			decodeAndRun(buf, () -> new NetworkEvent.Context(client, null));
		}
	}

	protected record PacketInfo<M>(int index, BiConsumer<M, FriendlyByteBuf> encoder,
								   Function<FriendlyByteBuf, M> decoder,
								   BiConsumer<M, Supplier<NetworkEvent.Context>> handler) {
	}
}
