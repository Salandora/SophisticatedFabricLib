package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.MobSpawnEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin_FinalizeSpawn {
    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void sophisticatedFabricLibrary$afterFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        MobSpawnEvents.AFTER_FINALIZE_SPAWN.invoker().onAfterFinalizeSpawn(new MobSpawnEvents.FinalizeSpawn((Mob) (Object) this, level, difficulty, spawnType, spawnData, dataTag));
    }
}
