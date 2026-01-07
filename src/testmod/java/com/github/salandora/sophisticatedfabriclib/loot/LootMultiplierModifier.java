package com.github.salandora.sophisticatedfabriclib.loot;

import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.LootModifier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootMultiplierModifier extends LootModifier {
	public static final Supplier<Codec<LootMultiplierModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
			.and(ExtraCodecs.POSITIVE_INT.fieldOf("multiplication_factor").forGetter(m -> m.multiplicationFactor))
			.apply(inst, LootMultiplierModifier::new)));

	private final int multiplicationFactor;

	public LootMultiplierModifier(final LootItemCondition[] conditionsIn, final int multiplicationFactor) {
		super(conditionsIn);
		this.multiplicationFactor = multiplicationFactor;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if (context.hasParam(LootContextParams.THIS_ENTITY)) {
			return generatedLoot.stream()
					.map(ItemStack::copy)
					.peek(stack -> stack.setCount(Math.min(stack.getMaxStackSize(), stack.getCount() * this.multiplicationFactor)))
					.collect(Collectors.toCollection(ObjectArrayList::new));
		}
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}