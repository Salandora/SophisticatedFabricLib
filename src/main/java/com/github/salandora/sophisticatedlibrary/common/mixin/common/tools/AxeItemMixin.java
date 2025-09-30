package com.github.salandora.sophisticatedlibrary.common.mixin.common.tools;

import com.github.salandora.sophisticatedlibrary.common.ItemAbilities;
import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin extends DiggerItem {
	protected AxeItemMixin(Tier tier, TagKey<Block> blocks, Properties properties) {
		super(tier, blocks, properties);
	}

	@Override
	public boolean sophisticatedLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
	}
}
