package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.entity;

import com.github.salandora.sophisticatedlibrary.util.LazyOptional;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.OptionalInt;
import java.util.function.Consumer;

public interface SophisticatedPlayer {
	private Player self() {
		return (Player)this;
	}

	default OptionalInt sophisticatedLibrary_openMenu(MenuProvider menuProvider, BlockPos pos) {
		return this.sophisticatedLibrary_openMenu(menuProvider, (buf) -> buf.writeBlockPos(pos));
	}

	default OptionalInt sophisticatedLibrary_openMenu(MenuProvider menu, Consumer<FriendlyByteBuf> context) {
		var screenHandlerFactory = new ExtendedScreenHandlerFactory() {
			@Override
			public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return menu.createMenu(i, inventory, player);
			}

			@Override
			public boolean shouldCloseCurrentScreen() {
				return menu.shouldCloseCurrentScreen();
			}

			@Override
			public Component getDisplayName() {
				return menu.getDisplayName();
			}

			@Override
			public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
				context.accept(buf);
			}
		};

		return this.self().openMenu(screenHandlerFactory);
	}

	default <T> LazyOptional<T> sophisticatedLibrary_getCapability(EntityApiLookup<T, ?> lookup) {
		return sophisticatedLibrary_getCapability(lookup, null);
	}

	default <T, C> LazyOptional<T> sophisticatedLibrary_getCapability(EntityApiLookup<T, C> lookup, @Nullable C context) {
		return LazyOptional.of(() -> lookup.find(self(), context));
	}
}
