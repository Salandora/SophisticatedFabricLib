package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity.SophisticatedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin_CustomData implements SophisticatedEntity {
	// TODO: Deprecated key, this is here for conversion, remove in future
	@Unique
	@Deprecated
	private static final String OLD_SOPHISTICATEDCOREDATA_NBT_KEY = "SophisticatedCoreData";

	@Unique
	private static final String SOPHISTICATEDFABRICLIBRARYDATA_NBT_KEY = "SophisticatedFabricLibraryData";

	@Unique
	private CompoundTag sophisticatedFabricLibrary$customData;

	@Override
	public CompoundTag sophisticatedFabricLibrary_getCustomData() {
		if (this.sophisticatedFabricLibrary$customData == null) {
			this.sophisticatedFabricLibrary$customData = new CompoundTag();
		}
		return this.sophisticatedFabricLibrary$customData;
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void sophisticatedCore$saveAdditionalData(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
		if (this.sophisticatedFabricLibrary$customData != null && !this.sophisticatedFabricLibrary$customData.isEmpty()) {
			compound.put(SOPHISTICATEDFABRICLIBRARYDATA_NBT_KEY, this.sophisticatedFabricLibrary$customData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void sophisticatedCore$readAdditionalData(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains(SOPHISTICATEDFABRICLIBRARYDATA_NBT_KEY)) {
			this.sophisticatedFabricLibrary$customData = compound.getCompound(SOPHISTICATEDFABRICLIBRARYDATA_NBT_KEY);
		} else if (compound.contains(OLD_SOPHISTICATEDCOREDATA_NBT_KEY)) {
			// TODO: Deprecated key, this is here for conversion, remove in future
			this.sophisticatedFabricLibrary$customData = compound.getCompound(OLD_SOPHISTICATEDCOREDATA_NBT_KEY);
		}
	}
}
