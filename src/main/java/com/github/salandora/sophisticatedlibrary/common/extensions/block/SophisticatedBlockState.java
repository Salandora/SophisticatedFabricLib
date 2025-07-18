package com.github.salandora.sophisticatedlibrary.common.extensions.block;

import com.github.salandora.sophisticatedlibrary.common.ItemAbility;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface SophisticatedBlockState {
	private BlockState self() {
		return (BlockState) this;
	}

	default BlockState sophisticatedLibrary$getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		return self().getBlock().sophisticatedLibrary$getToolModifiedState(self(), context, itemAbility, simulate);
	}
}
