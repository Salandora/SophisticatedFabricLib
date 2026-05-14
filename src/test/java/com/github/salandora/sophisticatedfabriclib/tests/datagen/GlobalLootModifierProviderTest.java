package com.github.salandora.sophisticatedfabriclib.tests.datagen;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.loot.SophisticatedLoot;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.GlobalLootModifierProvider;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.IGlobalLootModifier;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.LootModifier;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.LootTableIdCondition;
import com.google.common.base.Suppliers;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GlobalLootModifierProviderTest {
	private static final String TEST_MOD_ID = "global_loot_modifier_test";
	private static final String EXPECTED_MODIFIER_JSON = """
			{
			  "type": "global_loot_modifier_test:loot_multiplier",
			  "conditions": [
			    {
			      "condition": "sophisticatedfabriclib:loot_table_id",
			      "loot_table_id": "minecraft:chests/simple_dungeon"
			    }
			  ],
			  "multiplication_factor": 2
			}
			""";
	private static final String EXPECTED_GLOBAL_LIST_JSON = """
			{
			  "replace": false,
			  "entries": [
			    "global_loot_modifier_test:chests/simple_dungeon"
			  ]
			}
			""";
	private static final String EXPECTED_REPLACE_GLOBAL_LIST_JSON = """
			{
			  "replace": true,
			  "entries": [
			    "global_loot_modifier_test:chests/simple_dungeon"
			  ]
			}
			""";

	@BeforeAll
	static void setUp() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();

		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, SophisticatedFabricLib.id("loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);
		Registry.register(SophisticatedLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS, new ResourceLocation(TEST_MOD_ID, "loot_multiplier"), TestLootMultiplierModifier.CODEC.get());
	}

	private static HolderLookup.Provider registryProvider() {
		return HolderLookup.Provider.create(Stream.of(BuiltInRegistries.REGISTRY.asLookup()));
	}

	@Test
	void runWritesModifierAndGlobalListJson(@TempDir Path tempDir) throws IOException {
		runProvider(simpleDungeonProvider(tempDir));

		assertEquals(
				JsonParser.parseString(EXPECTED_MODIFIER_JSON),
				JsonParser.parseString(Files.readString(modifierPath(tempDir)))
		);
		assertEquals(
				JsonParser.parseString(EXPECTED_GLOBAL_LIST_JSON),
				JsonParser.parseString(Files.readString(globalListPath(tempDir)))
		);
	}

	@Test
	void runUsesReplaceFlagInGlobalListJson(@TempDir Path tempDir) throws IOException {
		GlobalLootModifierProvider provider = simpleDungeonProvider(tempDir);
		provider.replace(true);

		runProvider(provider);

		assertEquals(
				JsonParser.parseString(EXPECTED_REPLACE_GLOBAL_LIST_JSON),
				JsonParser.parseString(Files.readString(globalListPath(tempDir)))
		);
	}

	@Test
	void runRejectsDuplicateLootTables(@TempDir Path tempDir) {
		GlobalLootModifierProvider provider = new GlobalLootModifierProvider(new PackOutput(tempDir), CompletableFuture.completedFuture(registryProvider()), TEST_MOD_ID) {
			@Override
			protected void generate(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer) {
				acceptSimpleDungeonModifier(consumer);
				acceptSimpleDungeonModifier(consumer);
			}
		};

		CompletionException exception = assertThrows(CompletionException.class, () -> runProvider(provider));
		assertEquals(IllegalStateException.class, exception.getCause().getClass());
	}

	private static GlobalLootModifierProvider simpleDungeonProvider(Path outputDir) {
		return new GlobalLootModifierProvider(new PackOutput(outputDir), CompletableFuture.completedFuture(registryProvider()), TEST_MOD_ID) {
			@Override
			protected void generate(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer) {
				acceptSimpleDungeonModifier(consumer);
			}
		};
	}

	private static void acceptSimpleDungeonModifier(BiConsumer<ResourceLocation, IGlobalLootModifier> consumer) {
		consumer.accept(
				BuiltInLootTables.SIMPLE_DUNGEON,
				new TestLootMultiplierModifier(
						new LootItemCondition[] { new LootTableIdCondition.Builder(BuiltInLootTables.SIMPLE_DUNGEON).build() },
						2
				)
		);
	}

	private static void runProvider(GlobalLootModifierProvider provider) {
		provider.run(CachedOutput.NO_CACHE).join();
	}

	private static Path modifierPath(Path outputDir) {
		return outputDir.resolve("data").resolve(TEST_MOD_ID).resolve("loot_modifiers").resolve("chests").resolve("simple_dungeon.json");
	}

	private static Path globalListPath(Path outputDir) {
		return outputDir.resolve("data").resolve("sophisticated").resolve("loot_modifiers").resolve("global_loot_modifiers.json");
	}

	private static class TestLootMultiplierModifier extends LootModifier {
		private static final Supplier<Codec<TestLootMultiplierModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
				.and(ExtraCodecs.POSITIVE_INT.fieldOf("multiplication_factor").forGetter(m -> m.multiplicationFactor))
				.apply(inst, TestLootMultiplierModifier::new)));

		private final int multiplicationFactor;

		private TestLootMultiplierModifier(LootItemCondition[] conditions, int multiplicationFactor) {
			super(conditions);
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
}
