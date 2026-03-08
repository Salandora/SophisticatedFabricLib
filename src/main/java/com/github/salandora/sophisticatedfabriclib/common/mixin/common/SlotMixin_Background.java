package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.inventory.SophisticatedSlot;
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
    private Pair<ResourceLocation, ResourceLocation> sophisticatedFabricLibrary_background;

    @Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
    private void sophisticatedFabricLibrary$background(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> cir) {
        if (sophisticatedFabricLibrary_background != null) {
            cir.setReturnValue(sophisticatedFabricLibrary_background);
        }
    }

    @Override
    public Slot sophisticatedFabricLibrary_setBackground(ResourceLocation atlas, ResourceLocation sprite) {
        this.sophisticatedFabricLibrary_background = Pair.of(atlas, sprite);
        return (Slot) (Object) this;
    }

    @Unique
    @Override
    public int sophisticatedFabricLibrary_getSlotIndex() {
        return slot;
    }
}
