package com.github.salandora.sophisticatedlibrary.loot.api.v1;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

import java.util.function.Predicate;

public abstract class LootModifier implements IGlobalLootModifier {
	protected final LootItemCondition[] conditions;
	private final Predicate<LootContext> combinedConditions;

	protected static <T extends LootModifier> Products.P1<RecordCodecBuilder.Mu<T>, LootItemCondition[]> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions));
	}

	protected LootModifier(LootItemCondition[] conditionsIn) {
		this.conditions = conditionsIn;
		this.combinedConditions = LootItemConditions.andConditions(conditionsIn);
	}

	@Override
	public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		return this.combinedConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
	}

	protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
