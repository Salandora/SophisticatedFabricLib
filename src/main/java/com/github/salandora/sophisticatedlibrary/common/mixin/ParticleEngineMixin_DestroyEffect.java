package com.github.salandora.sophisticatedlibrary.common.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin_DestroyEffect {
    @Shadow
    protected ClientLevel level;

    @Redirect(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"))
    private boolean sophisticatedCore$addDestroyEffects(BlockState blockState, BlockPos pos) {
        return !blockState.sophisticatedLibrary_addDestroyEffects(level, pos, (ParticleEngine) (Object) this);
    }
}
