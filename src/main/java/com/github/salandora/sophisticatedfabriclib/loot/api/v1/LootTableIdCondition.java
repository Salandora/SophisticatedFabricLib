/*
 * This code comes from: https://github.com/neoforged/NeoForge/blob/1.21.1/src/main/java/net/neoforged/neoforge/common/loot/LootTableIdCondition.java
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.github.salandora.sophisticatedfabriclib.loot.api.v1;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class LootTableIdCondition implements LootItemCondition {
	public static final MapCodec<LootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
					.group(
							ResourceLocation.CODEC.fieldOf("loot_table_id").forGetter(idCondition -> idCondition.targetLootTableId))
					.apply(builder, LootTableIdCondition::new));
	public static final LootItemConditionType LOOT_TABLE_ID = new LootItemConditionType(CODEC);
	public static final ResourceLocation UNKNOWN_LOOT_TABLE = SophisticatedFabricLib.id("unknown_loot_table");

	private final ResourceLocation targetLootTableId;

	private LootTableIdCondition(final ResourceLocation targetLootTableId) {
		this.targetLootTableId = targetLootTableId;
	}

	@Override
	public LootItemConditionType getType() {
		return LOOT_TABLE_ID;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return lootContext.sophisticatedFabricLibrary_getQueriedLootTableId().equals(this.targetLootTableId);
	}

	public static Builder builder(ResourceLocation targetLootTableId) {
		return new Builder(targetLootTableId);
	}

	public static class Builder implements LootItemCondition.Builder {
		private final ResourceLocation targetLootTableId;

		public Builder(@NotNull ResourceLocation targetLootTableId) {
			this.targetLootTableId = targetLootTableId;
		}

		@Override
		public LootItemCondition build() {
			return new LootTableIdCondition(targetLootTableId);
		}
	}
}
