package com.github.salandora.sophisticatedlibrary.event.mixin.v1.client;

import com.github.salandora.sophisticatedlibrary.event.api.client.ClientRecipesUpdated;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin_UpdateRecipes {
	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Inject(method = "handleUpdateRecipes", at = @At("RETURN"))
	private void sophisticatedLibrary$handleUpdateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
		ClientRecipesUpdated.EVENT.invoker().onRecipesUpdated(this.recipeManager);
	}
}
