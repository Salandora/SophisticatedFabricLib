package com.github.salandora.sophisticatedfabriclib.common.mixin.common;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.extensions.block.SophisticatedIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public class IngredientMixin implements SophisticatedIngredient {
	@Shadow
	@Final
	private Ingredient.Value[] values;

	@Override
	public Ingredient.Value[] sophisticatedlibrary_getValues() {
		if (((Ingredient) (Object) this).getCustomIngredient() != null) {
			throw new IllegalStateException("Cannot retrieve values from custom ingredient!");
		} else {
			return this.values;
		}
	}
}
