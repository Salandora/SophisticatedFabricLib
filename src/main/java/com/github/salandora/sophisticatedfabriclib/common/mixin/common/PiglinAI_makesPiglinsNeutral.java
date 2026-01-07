package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAI_makesPiglinsNeutral {
	@Inject(
			method = "isWearingGold",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"
			),
			cancellable = true
	)
	private static void sophisticatedLibrary$makesPiglinsNeutral(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
		if (itemStack.sophisticatedLibrary_makesPiglinsNeutral(livingEntity)) {
			cir.setReturnValue(true);
		}
	}
}
