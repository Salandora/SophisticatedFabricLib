package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.client;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.client.ClientRawInputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin_KeyPressed {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "keyPress", at = @At("RETURN"), cancellable = true)
	public void sophisticatedFabricLibrary$keyPress(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		if (handle == this.minecraft.getWindow().getWindow()) {
			var result = ClientRawInputEvent.KEY_PRESSED.invoker().keyPressed(minecraft, key, scanCode, action, modifiers);
			if (result != InteractionResult.PASS)
				ci.cancel();
		}
	}
}
