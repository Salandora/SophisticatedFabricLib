package com.github.salandora.sophisticatedlibrary.network.api.v1;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.world.entity.player.Player;

public interface IPayloadContext {
	Player player();
	void enqueueWork(Runnable runnable);
	PacketSender responseSender();
}
