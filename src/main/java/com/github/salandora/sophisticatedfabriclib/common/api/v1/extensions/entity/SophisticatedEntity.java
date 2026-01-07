package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

import javax.annotation.Nullable;
import java.util.Collection;

public interface SophisticatedEntity {
	default CompoundTag sophisticatedLibrary_getCustomData() {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	@Nullable
	default Collection<ItemEntity> sophisticatedLibrary_captureDrops() {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	default Collection<ItemEntity> sophisticatedLibrary_captureDrops(@Nullable Collection<ItemEntity> value) {
		throw new RuntimeException("This should have been implemented via mixin.");
	}

	default void sophisticatedLibrary_invalidateCaps() {
	}
}
