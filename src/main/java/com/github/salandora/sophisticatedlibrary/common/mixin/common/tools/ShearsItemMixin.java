package com.github.salandora.sophisticatedlibrary.common.mixin.common.tools;

import com.github.salandora.sophisticatedlibrary.common.api.v1.ItemAbilities;
import com.github.salandora.sophisticatedlibrary.common.api.v1.ItemAbility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {
	public ShearsItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public boolean sophisticatedLibrary_canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility);
	}
}
