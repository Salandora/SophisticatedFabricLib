package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.item;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbility;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

public interface SophisticatedItem {
	default boolean sophisticatedFabricLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return false;
	}

	default float sophisticatedFabricLibrary_getXpRepairRatio(ItemStack stack) {
		return 1.0F;
	}

	// This gets overridden in BackpackItem
	default boolean sophisticatedFabricLibrary_onDroppedByPlayer(ItemStack stack, Player player) {
		return true;
	}

	@ApiStatus.OverrideOnly
	default int sophisticatedFabricLibrary_getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
		return burnTime != null ? burnTime : 0;
	}

	default InteractionResult sophisticatedFabricLibrary_onItemUseFirst(ItemStack stack, UseOnContext context) {
		return InteractionResult.PASS;
	}

	default @Nullable FoodProperties sophisticatedFabricLibrary_getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
		return stack.get(DataComponents.FOOD);
	}

	@ApiStatus.OverrideOnly
	default int sophisticatedFabricLibrary_getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		return stack.getEnchantments().getLevel(enchantment);
	}

	default boolean sophisticatedFabricLibrary_shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.equals(newStack);
	}

	default boolean sophisticatedFabricLibrary_makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return false;
	}
}
