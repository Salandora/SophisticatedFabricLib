package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import net.minecraft.core.component.DataComponentHolder;
import com.github.salandora.sophisticatedlibrary.common.extensions.component.SophisticatedDataComponentHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin extends SophisticatedDataComponentHolder {
}
