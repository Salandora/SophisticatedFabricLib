package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.item;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbility;
import com.github.salandora.sophisticatedfabriclib.transfer.api.v1.MutableContainerItemContext;
import com.github.salandora.sophisticatedfabriclib.util.LazyOptional;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import javax.annotation.Nullable;

public interface SophisticatedItemStack {
	private ItemStack self() {
		return (ItemStack) this;
	}

	default boolean sophisticatedFabricLibrary_canPerformAction(ItemAbility itemAbility) {
		return self().getItem().sophisticatedFabricLibrary_canPerformAction(self(), itemAbility);
	}

	default float sophisticatedFabricLibrary_getXpRepairRatio() {
		return this.self().getItem().sophisticatedFabricLibrary_getXpRepairRatio(this.self());
	}

	default boolean sophisticatedFabricLibrary_onDroppedByPlayer(Player player) {
		return self().getItem().sophisticatedFabricLibrary_onDroppedByPlayer(self(), player);
	}

	default int sophisticatedFabricLibrary_getBurnTime(@Nullable RecipeType<?> recipeType) {
		if (this.self().isEmpty()) {
			return 0;
		}

		int burnTime = this.self().getItem().sophisticatedFabricLibrary_getBurnTime(this.self(), recipeType);
		if (burnTime < 0) {
			String itemId = String.valueOf(BuiltInRegistries.ITEM.getKey(this.self().getItem()));
			throw new IllegalStateException("Stack of item " + itemId + " has a negative burn time");
		}

		return burnTime;
	}

	default void sophisticatedFabricLibrary_onArmorTick(Level level, Player player) {
		self().getItem().sophisticatedFabricLibrary_onArmorTick(self(), level, player);
	}

	default InteractionResult sophisticatedFabricLibrary_onItemUseFirst(UseOnContext context) {
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (!player.getAbilities().mayBuild && !self().hasAdventureModePlaceTagForBlock(context.getLevel().registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(context.getLevel(), pos, false))) {
			return InteractionResult.PASS;
		} else {
			Item item = self().getItem();
			InteractionResult result = item.sophisticatedFabricLibrary_onItemUseFirst(self(), context);
			if (result == InteractionResult.SUCCESS) {
				player.awardStat(Stats.ITEM_USED.get(item));
			}

			return result;
		}
	}

	default boolean sophisticatedFabricLibrary_makesPiglinsNeutral(LivingEntity wearer) {
		return self().getItem().sophisticatedFabricLibrary_makesPiglinsNeutral(self(), wearer);
	}

	default <T> LazyOptional<T> sophisticatedFabricLibrary_getCapability(ItemApiLookup<T, ContainerItemContext> lookup) {
		ContainerItemContext context = MutableContainerItemContext.ofSingleStack(self());
		return LazyOptional.of(() -> context.find(lookup));
	}

	default <T> LazyOptional<T> sophisticatedFabricLibrary_getLazyCapability(ItemApiLookup<LazyOptional<T>, Void> lookup) {
		// This can be null!
		LazyOptional<T> capability = lookup.find(self(), null);
		return capability == null ? LazyOptional.empty() : capability;

	}
}
