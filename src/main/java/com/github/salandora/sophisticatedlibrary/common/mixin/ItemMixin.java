package com.github.salandora.sophisticatedlibrary.common.mixin;

import com.github.salandora.sophisticatedlibrary.common.extensions.item.SophisticatedItemExtension;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements SophisticatedItemExtension {
}
