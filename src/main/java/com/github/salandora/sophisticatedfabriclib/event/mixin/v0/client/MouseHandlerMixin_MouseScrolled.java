package com.github.salandora.sophisticatedfabriclib.event.mixin.v0.client;

import com.github.salandora.sophisticatedfabriclib.event.api.v0.client.ClientRawInputEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin_MouseScrolled {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
			method = "onScroll",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MouseHandler;accumulatedScroll:D",
					ordinal = 3,
					shift = At.Shift.AFTER,
					opcode = Opcodes.GETFIELD
			),
			cancellable = true
	)
    private void  sophisticatedLibrary$onScroll(long handle, double xOffset, double yOffset, CallbackInfo ci, @Local(ordinal = 2) double delta) {
        if (handle == this.minecraft.getWindow().getWindow()) {
            var result = ClientRawInputEvent.MOUSE_SCROLLED.invoker().mouseScrolled(minecraft, delta);
            if (result != InteractionResult.PASS)
                ci.cancel();
        }
    }
}
