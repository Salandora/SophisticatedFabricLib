package com.github.salandora.sophisticatedfabriclib.loot.mixin;

import com.github.salandora.sophisticatedfabriclib.loot.SophisticatedLoot;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.extensions.SophisticatedLootTable;
import com.github.salandora.sophisticatedfabriclib.loot.api.v1.extensions.SophisticatedLootTableBuilder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin  implements SophisticatedLootTable {
	@Shadow
	public abstract void getRandomItemsRaw(LootContext context, Consumer<ItemStack> output);

	@Shadow
	public static Consumer<ItemStack> createStackSplitter(ServerLevel level, Consumer<ItemStack> output) {
		return null;
	}

	@Shadow protected abstract ObjectArrayList<ItemStack> getRandomItems(LootContext context);

	@Shadow
	@Final
	private Optional<ResourceLocation> randomSequence;

	@Shadow
	public abstract ObjectArrayList<ItemStack> getRandomItems(LootParams params);

	@Unique
	private ResourceLocation sophisticatedFabricLibrary$id;

	@Override
	public void sophisticatedFabricLibrary_setId(ResourceLocation id) {
		this.sophisticatedFabricLibrary$id = id;
	}

	@Inject(
			method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	public void getRandomItems(LootParams params, long seed, Consumer<ItemStack> output, CallbackInfo ci) {
		this.getRandomItems((new LootContext.Builder(params)).withOptionalRandomSeed(seed).create(this.randomSequence)).forEach(output);
		ci.cancel();
	}

	@Inject(
			method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	public void getRandomItems(LootParams params, Consumer<ItemStack> output, CallbackInfo ci) {
		this.getRandomItems(params).forEach(output);
		ci.cancel();
	}

	@Inject(
			method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	public void getRandomItems(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
		this.getRandomItems(context).forEach(output);
		ci.cancel();
	}

	@Inject(
			method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void getRandomItems(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
		ObjectArrayList<ItemStack> objectArrayList = new ObjectArrayList<>();
		this.getRandomItemsRaw(context, createStackSplitter(context.getLevel(), objectArrayList::add));
		objectArrayList = SophisticatedLoot.modifyLoot(sophisticatedFabricLibrary$id, objectArrayList, context);
		cir.setReturnValue(objectArrayList);
	}

	@Mixin(LootTable.Builder.class)
	public static class BuilderMixin implements SophisticatedLootTableBuilder {
		@Unique
		private ResourceLocation sophisticatedFabricLibrary$id;

		@Override
		public void sophisticatedFabricLibrary_setId(ResourceLocation id) {
			this.sophisticatedFabricLibrary$id = id;
		}

		@ModifyReturnValue(method = "build", at = @At("RETURN"))
		private LootTable sophisticatedFabricLibrary$injectLootTableId(LootTable table) {
			if (this.sophisticatedFabricLibrary$id != null) {
				((SophisticatedLootTable) table).sophisticatedFabricLibrary_setId(this.sophisticatedFabricLibrary$id);
			}

			return table;
		}
	}
}
