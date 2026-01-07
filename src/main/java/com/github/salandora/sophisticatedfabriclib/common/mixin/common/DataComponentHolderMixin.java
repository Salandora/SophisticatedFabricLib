package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.component.SophisticatedDataComponentHolder;
import net.minecraft.core.component.DataComponentHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin extends SophisticatedDataComponentHolder {
}
