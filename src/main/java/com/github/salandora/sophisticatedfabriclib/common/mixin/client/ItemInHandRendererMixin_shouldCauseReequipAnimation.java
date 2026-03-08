package com.github.salandora.sophisticatedfabriclib.common.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin_shouldCauseReequipAnimation {
	@Shadow
	private ItemStack mainHandItem;

	@Shadow
	private ItemStack offHandItem;

	@Unique
	private int slotMainHand = 0;

	@Unique
	private boolean sophisticatedFabricLibrary_shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot) {
		boolean fromEmpty = from.isEmpty();
		boolean toEmpty = to.isEmpty();

		if (fromEmpty && toEmpty) return false;
		if (fromEmpty || toEmpty) return true;

		boolean changed = false;
		if (slot != -1) {
			changed = slot != slotMainHand;
			slotMainHand = slot;
		}
		return from.getItem().sophisticatedFabricLibrary_shouldCauseReequipAnimation(from, to, changed);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
			)
	)
	private void sophisticatedFabricLibrary$shouldCauseReequipAnimation(CallbackInfo ci,
																@Local LocalPlayer localPlayer,
																@Local(ordinal = 0) ItemStack mainHandItem,
																@Local(ordinal = 1) ItemStack offHandItem) {
		boolean reequipMain = sophisticatedFabricLibrary_shouldCauseReequipAnimation(this.mainHandItem, mainHandItem, localPlayer.getInventory().selected);
		if (!reequipMain && this.mainHandItem != mainHandItem) {
			this.mainHandItem = mainHandItem;
		}

		boolean reequipOff = sophisticatedFabricLibrary_shouldCauseReequipAnimation(this.offHandItem, offHandItem, -1);
		if (!reequipOff && this.offHandItem != offHandItem) {
			this.offHandItem = offHandItem;
		}
	}
}
