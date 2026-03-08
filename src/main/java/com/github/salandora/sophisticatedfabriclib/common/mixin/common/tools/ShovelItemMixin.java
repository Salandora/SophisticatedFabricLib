package com.github.salandora.sophisticatedfabriclib.common.mixin.common.tools;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbilities;
import com.github.salandora.sophisticatedfabriclib.common.api.v1.ItemAbility;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShovelItem.class)
public class ShovelItemMixin extends DiggerItem {
	protected ShovelItemMixin(Tier tier, TagKey<Block> blocks, Properties properties) {
		super(tier, blocks, properties);
	}

	@Override
	public boolean sophisticatedFabricLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
	}
}
