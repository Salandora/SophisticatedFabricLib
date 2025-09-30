package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.extensions.inventory.SophisticatedSlot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin_Background implements SophisticatedSlot {
    @Shadow
    @Final
    private int slot;

    @Unique
    private Pair<ResourceLocation, ResourceLocation> sophisticatedLibrary_background;

    @Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
    private void sophisticatedLibrary$background(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> cir) {
        if (sophisticatedLibrary_background != null) {
            cir.setReturnValue(sophisticatedLibrary_background);
        }
    }

    @Override
    public Slot sophisticatedLibrary_setBackground(ResourceLocation atlas, ResourceLocation sprite) {
        this.sophisticatedLibrary_background = Pair.of(atlas, sprite);
        return (Slot) (Object) this;
    }

    @Unique
    @Override
    public int sophisticatedLibrary_getSlotIndex() {
        return slot;
    }
}
