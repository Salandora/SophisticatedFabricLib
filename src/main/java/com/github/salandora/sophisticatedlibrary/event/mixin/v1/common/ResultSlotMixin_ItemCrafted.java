package com.github.salandora.sophisticatedlibrary.event.mixin.v1.common;

import com.github.salandora.sophisticatedlibrary.event.api.common.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ResultSlotMixin_ItemCrafted {
	@Shadow @Final private CraftingContainer craftSlots;

	@Shadow @Final private Player player;

	@Inject(method = "onTake", at = @At("HEAD"))
	public void sophisticatedCore_onTake(Player player, ItemStack stack, CallbackInfo callbackInfo) {
		PlayerEvents.ITEM_CRAFTED.invoker().onItemCrafted(player, stack, craftSlots);
	}

	@Inject(method = "onQuickCraft", at = @At("HEAD"))
	public void sophisticatedCore_onQuickCraft(ItemStack stack, int amount, CallbackInfo ci) {
		PlayerEvents.ITEM_CRAFTED.invoker().onItemCrafted(player, stack, craftSlots);
	}
}
