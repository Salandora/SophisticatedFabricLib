package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.entity.SophisticatedEntity;
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
	private static final String SOPHISTICATEDLIBRARYDATA_NBT_KEY = "SophisticatedLibraryData";

	@Unique
	private CompoundTag sophisticatedLibrary$customData;

	@Override
	public CompoundTag sophisticatedLibrary_getCustomData() {
		if (this.sophisticatedLibrary$customData == null) {
			this.sophisticatedLibrary$customData = new CompoundTag();
		}
		return this.sophisticatedLibrary$customData;
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void sophisticatedLibrary$saveAdditionalData(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
		if (this.sophisticatedLibrary$customData != null && !this.sophisticatedLibrary$customData.isEmpty()) {
			compound.put(SOPHISTICATEDLIBRARYDATA_NBT_KEY, this.sophisticatedLibrary$customData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void sophisticatedLibrary$readAdditionalData(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains(SOPHISTICATEDLIBRARYDATA_NBT_KEY)) {
			this.sophisticatedLibrary$customData = compound.getCompound(SOPHISTICATEDLIBRARYDATA_NBT_KEY);
		} else if (compound.contains(OLD_SOPHISTICATEDCOREDATA_NBT_KEY)) {
			// TODO: Deprecated key, this is here for conversion, remove in future
			this.sophisticatedLibrary$customData = compound.getCompound(OLD_SOPHISTICATEDCOREDATA_NBT_KEY);
		}
	}
}
