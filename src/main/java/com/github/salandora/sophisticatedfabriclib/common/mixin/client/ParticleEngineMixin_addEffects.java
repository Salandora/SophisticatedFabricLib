package com.github.salandora.sophisticatedfabriclib.common.mixin.client;

import com.github.salandora.sophisticatedfabriclib.common.api.v1.client.ClientExtensionManager;
import com.github.salandora.sophisticatedfabriclib.common.api.v1.client.IClientBlockExtensions;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin_addEffects {
    @Shadow
    protected ClientLevel level;

    @WrapOperation(
			method = "destroy",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnParticlesOnBreak()Z"
			)
	)
    private boolean sophisticatedFabricLibrary$addDestroyEffects(BlockState blockState, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
		IClientBlockExtensions extensions = ClientExtensionManager.BLOCK_EXTENSIONS.find(level, pos, blockState, null, null);
		if (extensions != null) {
			return extensions.addDestroyEffects(blockState, level, pos, (ParticleEngine) (Object) this);
		}
        return original.call(blockState);
    }

	@WrapMethod(
			method = "crack"
	)
	private void sophisticatedFabricLibrary$addHitEffects(BlockPos pos, Direction side, Operation<Void> original) {
		BlockState blockState = this.level.getBlockState(pos);
		IClientBlockExtensions extensions = ClientExtensionManager.BLOCK_EXTENSIONS.find(level, pos, blockState, null, null);
		if (extensions != null && !extensions.addHitEffects(blockState, level, Minecraft.getInstance().hitResult, (ParticleEngine) (Object) this)) {
			original.call(pos, side);
		}
	}
}
