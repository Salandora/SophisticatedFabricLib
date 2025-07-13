package com.github.salandora.sophisticatedlibrary.common.mixin;

import com.github.salandora.sophisticatedlibrary.common.extensions.item.SophisticatedItemStackExtension;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements SophisticatedItemStackExtension {
}
