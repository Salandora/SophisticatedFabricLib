package com.github.salandora.sophisticatedlibrary.event.mixin.v1.client;

import com.github.salandora.sophisticatedlibrary.event.api.client.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin_LevelLoad {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void sophisticatedLibrary$construct(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, int i, int j, Supplier<ProfilerFiller> supplier, LevelRenderer levelRenderer, boolean bl, long l, CallbackInfo ci) {
        ClientLifecycleEvents.CLIENT_LEVEL_LOAD.invoker().onWorldLoad(minecraft, (ClientLevel) (Object) this);
    }
}
