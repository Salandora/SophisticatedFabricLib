package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.common.LivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin_Drops extends Entity {
    @Shadow
    protected int lastHurtByPlayerTime;

	@Unique
	private int sophisticatedLibrary$lootingLevel;

    public LivingEntityMixin_Drops(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
			method = "dropAllDeathLoot",
			at = @At("HEAD")
	)
    private void sophisticatedLibrary$captureDrops(DamageSource damageSource, CallbackInfo ci) {
        this.sophisticatedLibrary_captureDrops(new ArrayList<>());
    }

	@ModifyVariable(
			method = "dropAllDeathLoot",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/LivingEntity;lastHurtByPlayerTime:I",
					opcode = Opcodes.GETFIELD
			)
	)
	private int sophisticatedLibrary$getLootingLevel(int lootingLevel) {
		this.sophisticatedLibrary$lootingLevel = lootingLevel;
		return lootingLevel;
	}

    @Inject(
			method = "dropAllDeathLoot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience()V"
			)
	)
    private void sophisticatedLibrary$dropCapturedDrops(DamageSource damageSource, CallbackInfo ci) {
        Collection<ItemEntity> drops = this.sophisticatedLibrary_captureDrops(null);
        if (!LivingEntityEvents.DROPS.invoker().onLivingEntityDrops((LivingEntity) (Object) this, damageSource, drops, this.sophisticatedLibrary$lootingLevel,lastHurtByPlayerTime > 0))
            drops.forEach(level()::addFreshEntity);
    }
}
