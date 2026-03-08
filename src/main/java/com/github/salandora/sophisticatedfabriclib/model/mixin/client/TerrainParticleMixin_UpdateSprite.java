package com.github.salandora.sophisticatedfabriclib.model.mixin.client;

import com.github.salandora.sophisticatedfabriclib.model.api.v1.client.render.CustomParticleIcon;
import com.github.salandora.sophisticatedfabriclib.model.api.v1.extensions.client.particle.SophisticatedTerrainParticle;
import com.github.salandora.sophisticatedfabriclib.model.api.v1.util.ModelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TerrainParticle.class)
public abstract class TerrainParticleMixin_UpdateSprite extends TextureSheetParticle implements SophisticatedTerrainParticle {
	protected TerrainParticleMixin_UpdateSprite(ClientLevel clientLevel, double d, double e, double f) {
		super(clientLevel, d, e, f);
	}

	@Override
	public TerrainParticle sophisticatedFabricLibrary_updateSprite(BlockState state, @Nullable BlockPos pos) {
		if (pos != null) {
			BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
			if (shaper.getBlockModel(state) instanceof CustomParticleIcon model) {
				ModelData data = model.getModelData(Minecraft.getInstance().level, pos, state, ModelData.EMPTY);
				this.setSprite(model.getParticleIcon(data));
			}
		}

		return (TerrainParticle) (Object) this;
	}
}
