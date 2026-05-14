package com.github.salandora.sophisticatedfabriclib.gametests;

import com.github.salandora.sophisticatedfabriclib.gametests.util.LootMultiplierModifier;
import com.github.salandora.sophisticatedfabriclib.loot.SophisticatedLoot;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedfabriclib.util.DeferredHolder;
import com.github.salandora.sophisticatedfabriclib.util.DeferredRegister;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlobalLootModifierTest {
	private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER = DeferredRegister.create(SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "global_loot_modifier_test");
	private static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<LootMultiplierModifier>> LOOT_MULTIPLIER_MODIFIER = GLOBAL_LOOT_MODIFIER.register("loot_multiplier", LootMultiplierModifier.CODEC);

	static {
		GLOBAL_LOOT_MODIFIER.register();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void globalLootModifierDoublesSimpleDungeonLoot(GameTestHelper context) {
		long seed = 123;

		AtomicReference<Map<Item, Integer>> stacks = new AtomicReference<>();
		AtomicReference<Map<Item, Integer>> expected = new AtomicReference<>();

		context.startSequence()
				.thenExecute(() -> context.setBlock(0, 1, 0, Blocks.CHEST))
				.thenExecute(() -> {
					ChestBlockEntity chestBlockEntity = (ChestBlockEntity) context.getBlockEntity(new BlockPos(0, 1, 0));
					chestBlockEntity.setLootTable(BuiltInLootTables.SIMPLE_DUNGEON, seed);
					chestBlockEntity.unpackLootTable(context.makeMockPlayer());
					stacks.set(IntStream.range(0, 27)
							.mapToObj(chestBlockEntity::getItem)
							.filter(Predicate.not(ItemStack::isEmpty))
							.collect(
									Collectors.toMap(
										ItemStack::getItem,
										ItemStack::getCount,
										Integer::sum
									)
							)
					);
				})
				.thenExecute(() ->
						expected.set(context.getLevel().getServer().getLootData().getLootTable(BuiltInLootTables.SIMPLE_DUNGEON)
							.getRandomItems(new LootParams.Builder(context.getLevel())
									.withParameter(LootContextParams.ORIGIN, context.absoluteVec(new Vec3(0, 2, 0)))
									.create(LootContextParamSets.CHEST), seed)
							.stream()
							.collect(
									Collectors.toMap(
											ItemStack::getItem,
											stack -> Math.min(stack.getMaxStackSize(), stack.getCount() * 2),
											Integer::sum
									)
							)
						)
				)
				.thenExecute(() -> context.assertTrue(
						stacks.get().equals(expected.get()),
						"Stacks don't match. Expected " + expected.get() + ", but was " + stacks.get())
				)
				.thenSucceed();
	}
}
