package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import com.github.salandora.sophisticatedlibrary.common.extensions.entity.SophisticatedPlayer;

@Mixin(Player.class)
public abstract class PlayerMixin implements SophisticatedPlayer {
}
