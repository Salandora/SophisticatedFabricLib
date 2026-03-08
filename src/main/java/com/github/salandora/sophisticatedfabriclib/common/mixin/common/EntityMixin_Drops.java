package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity.SophisticatedEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(Entity.class)
public class EntityMixin_Drops implements SophisticatedEntity {
	@Unique
	private Collection<ItemEntity> sophisticatedFabricLibrary$captureDrops = null;

	@WrapOperation(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean sophisticatedFabricLibrary$captureDrops(Level level, Entity entity, Operation<Boolean> original) {
		if (sophisticatedFabricLibrary_captureDrops() != null && entity instanceof ItemEntity item) {
			sophisticatedFabricLibrary_captureDrops().add(item);
			return false;
		}
		return original.call(level, entity);
	}

	@Unique
	@Override
	public Collection<ItemEntity> sophisticatedFabricLibrary_captureDrops() {
		return this.sophisticatedFabricLibrary$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> sophisticatedFabricLibrary_captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = this.sophisticatedFabricLibrary$captureDrops;
		this.sophisticatedFabricLibrary$captureDrops = value;
		return ret;
	}
}
