package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.extensions.entity.SophisticatedEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
	private Collection<ItemEntity> sophisticatedLibrary$captureDrops = null;

	@WrapWithCondition(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean sophisticatedLibrary$captureDrops(Level level, Entity entity) {
		if (sophisticatedLibrary_captureDrops() != null && entity instanceof ItemEntity item) {
			sophisticatedLibrary_captureDrops().add(item);
			return false;
		}
		return true;
	}

	@Unique
	@Override
	public Collection<ItemEntity> sophisticatedLibrary_captureDrops() {
		return this.sophisticatedLibrary$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> sophisticatedLibrary_captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = this.sophisticatedLibrary$captureDrops;
		this.sophisticatedLibrary$captureDrops = value;
		return ret;
	}
}
