package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.entity.SophisticatedPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements SophisticatedPlayer {
}
