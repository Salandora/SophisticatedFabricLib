package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v1.common.LivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin_Drops extends Entity {
    @Shadow
    protected int lastHurtByPlayerTime;

    public LivingEntityMixin_Drops(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
			method = "dropAllDeathLoot",
			at = @At("HEAD")
	)
    private void sophisticatedFabricLibrary$captureDrops(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        this.sophisticatedFabricLibrary_captureDrops(new ArrayList<>());
    }

    @Inject(
			method = "dropAllDeathLoot",
			at = @At(value = "RETURN")
	)
    private void sophisticatedFabricLibrary$dropCapturedDrops(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        Collection<ItemEntity> drops = this.sophisticatedFabricLibrary_captureDrops(null);
        if (!LivingEntityEvents.DROPS.invoker().onLivingEntityDrops((LivingEntity) (Object) this, damageSource, drops,lastHurtByPlayerTime > 0))
            drops.forEach(level::addFreshEntity);
    }
}
