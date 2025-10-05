package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.component.SophisticatedDataComponentHolder;
import net.minecraft.core.component.DataComponentHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin extends SophisticatedDataComponentHolder {
}
