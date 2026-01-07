package com.github.salandora.sophisticatedfabriclib.network.api.v0;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public interface NetworkEvent {
	final class Context {
		private final Executor exec;
		@Nullable
		private final ServerPlayer sender;
		private boolean handled;

		public Context(Executor exec, @Nullable ServerPlayer sender) {
			this.exec = exec;
			this.sender = sender;
			this.handled = false;
		}

		public void enqueueWork(Runnable runnable) {
			exec.execute(runnable);
		}

		@Nullable
		public ServerPlayer getSender() {
			return sender;
		}

		public NetworkDirection getDirection() {
			return sender == null ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
		}

		public boolean getPacketHandled() {
			return this.handled;
		}

		public void setPacketHandled(boolean handled) {
			this.handled = handled;
		}
	}
}
