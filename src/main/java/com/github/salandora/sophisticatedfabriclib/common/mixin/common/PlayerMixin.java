package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity.SophisticatedPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements SophisticatedPlayer {
}
