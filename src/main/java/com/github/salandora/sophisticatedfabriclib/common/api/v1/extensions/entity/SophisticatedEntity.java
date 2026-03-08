package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

import javax.annotation.Nullable;
import java.util.Collection;

public interface SophisticatedEntity {
	default CompoundTag sophisticatedFabricLibrary_getCustomData() {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	@Nullable
	default Collection<ItemEntity> sophisticatedFabricLibrary_captureDrops() {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	default Collection<ItemEntity> sophisticatedFabricLibrary_captureDrops(@Nullable Collection<ItemEntity> value) {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	default void sophisticatedFabricLibrary_invalidateCaps() {
	}
}
