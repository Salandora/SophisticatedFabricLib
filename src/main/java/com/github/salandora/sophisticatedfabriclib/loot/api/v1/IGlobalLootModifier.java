/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.20.1/src/main/java/net/minecraftforge/common/loot/IGlobalLootModifier.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.loot.api.v1;

import com.github.salandora.sophisticatedfabriclib.loot.SophisticatedLoot;
import com.github.salandora.sophisticatedfabriclib.loot.impl.LootModifierManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Function;

public interface IGlobalLootModifier {
	Codec<IGlobalLootModifier> DIRECT_CODEC = SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS
			.byNameCodec()
			.dispatch(IGlobalLootModifier::codec, Function.identity());

	Codec<LootItemCondition[]> LOOT_CONDITIONS_CODEC = Codec.PASSTHROUGH.flatXmap(
			d -> {
				try {
					LootItemCondition[] conditions = LootModifierManager.GSON.fromJson(getJson(d), LootItemCondition[].class);
					return DataResult.success(conditions);
				} catch (JsonSyntaxException e) {
					// TODO: add meaningful message
					return DataResult.error(e::getMessage);
				}
			},
			conditions -> {
				try {
					JsonElement element = LootModifierManager.GSON.toJsonTree(conditions);
					return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
				} catch (JsonSyntaxException e) {
					// TODO: add meaningful message
					return DataResult.error(e::getMessage);
				}
			}
	);

	static <U> JsonElement getJson(Dynamic<U> dynamic) {
		return dynamic.getValue() instanceof JsonElement ? (JsonElement) dynamic.getValue() : dynamic.getOps().convertTo(JsonOps.INSTANCE, dynamic.getValue());
	}

	ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);

	Codec<? extends IGlobalLootModifier> codec();
}
