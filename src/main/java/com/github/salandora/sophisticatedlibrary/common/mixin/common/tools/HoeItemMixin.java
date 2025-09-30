package com.github.salandora.sophisticatedlibrary.common.mixin.common.tools;

import com.github.salandora.sophisticatedlibrary.common.ItemAbilities;
import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoeItem.class)
public class HoeItemMixin extends DiggerItem {
	protected HoeItemMixin(Tier tier, TagKey<Block> blocks, Properties properties) {
		super(tier, blocks, properties);
	}

	@Override
	public boolean sophisticatedLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility);
	}
}
