package com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.block;

import net.minecraft.world.item.crafting.Ingredient;

public interface SophisticatedIngredient {
	default Ingredient.Value[] sophisticatedlibrary_getValues() { throw new RuntimeException("Should have been overriden by mixin."); }
}
