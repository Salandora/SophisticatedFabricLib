package com.github.salandora.sophisticatedlibrary.loot.api.v1;

import com.github.salandora.sophisticatedlibrary.loot.SophisticatedLoot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Function;

public interface IGlobalLootModifier {
	Codec<IGlobalLootModifier> DIRECT_CODEC = SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS
			.byNameCodec()
			.dispatch(IGlobalLootModifier::codec, Function.identity());

	Codec<LootItemCondition[]> LOOT_CONDITIONS_CODEC = LootItemCondition.DIRECT_CODEC
			.listOf()
			.xmap(list -> list.toArray(LootItemCondition[]::new), List::of);

	ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);

	MapCodec<? extends IGlobalLootModifier> codec();
}
