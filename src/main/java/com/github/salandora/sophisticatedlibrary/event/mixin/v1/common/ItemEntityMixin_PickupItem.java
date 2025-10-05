package com.github.salandora.sophisticatedlibrary.event.mixin.v1.common;

import com.github.salandora.sophisticatedlibrary.event.api.common.ItemEntityEvents;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin_PickupItem {
    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void sophisticatedLibrary$pickupItem(Player player, CallbackInfo ci, @Share("cachedStack") LocalRef<ItemStack> cachedStack) {
        cachedStack.set(getItem().copy());
        var canPickup = ItemEntityEvents.CAN_PICKUP.invoker().canPickup(player, (ItemEntity) (Object) this, getItem());
        if (canPickup != InteractionResult.PASS) {
            ci.cancel();
        }
    }
}
