package com.github.salandora.sophisticatedlibrary.common.api.v1.extensions.item;

import com.github.salandora.sophisticatedlibrary.common.api.v1.ItemAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

public interface SophisticatedItemStack {
	private ItemStack self() {
		return (ItemStack) this;
	}

	default boolean sophisticatedLibrary_canPerformAction(ItemAbility itemAbility) {
		return self().getItem().sophisticatedLibrary_canPerformAction(self(), itemAbility);
	}

	default float sophisticatedLibrary_getXpRepairRatio() {
		return this.self().getItem().sophisticatedLibrary_getXpRepairRatio(this.self());
	}

	default boolean sophisticatedLibrary_onDroppedByPlayer(Player player) {
		return self().getItem().sophisticatedLibrary_onDroppedByPlayer(self(), player);
	}

	default int sophisticatedLibrary_getBurnTime(@Nullable RecipeType<?> recipeType) {
		if (this.self().isEmpty()) {
			return 0;
		}

		int burnTime = this.self().getItem().sophisticatedLibrary_getBurnTime(this.self(), recipeType);
		if (burnTime < 0) {
			String itemId = String.valueOf(BuiltInRegistries.ITEM.getKey(this.self().getItem()));
			throw new IllegalStateException("Stack of item " + itemId + " has a negative burn time");
		}

		return burnTime;
	}

	default InteractionResult sophisticatedLibrary_onItemUseFirst(UseOnContext context) {
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (!player.getAbilities().mayBuild && !self().canPlaceOnBlockInAdventureMode(new BlockInWorld(context.getLevel(), pos, false))) {
			return InteractionResult.PASS;
		} else {
			Item item = self().getItem();
			InteractionResult result = item.sophisticatedLibrary_onItemUseFirst(self(), context);
			if (result == InteractionResult.SUCCESS) {
				player.awardStat(Stats.ITEM_USED.get(item));
			}

			return result;
		}
	}

	@ApiStatus.OverrideOnly
	default int sophisticatedLibrary_getEnchantmentLevel(Holder<Enchantment> enchantment) {
		return self().getItem().sophisticatedLibrary_getEnchantmentLevel(self(), enchantment);
	}

	default boolean sophisticatedLibrary_makesPiglinsNeutral(LivingEntity wearer) {
		return self().getItem().sophisticatedLibrary_makesPiglinsNeutral(self(), wearer);
	}
}
