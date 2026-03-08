package com.github.salandora.sophisticatedfabriclib.common.mixin.common.tools;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbilities;
import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbility;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin extends DiggerItem {
	protected PickaxeItemMixin(float attackDamageModifier, float attackSpeedModifier, Tier tier, TagKey<Block> blocks, Properties properties) {
		super(attackDamageModifier, attackSpeedModifier, tier, blocks, properties);
	}

	@Override
	public boolean sophisticatedFabricLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility);
	}
}
