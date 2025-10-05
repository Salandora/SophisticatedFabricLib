package com.github.salandora.sophisticatedlibrary.model.api.v1.client.render;

import com.github.salandora.sophisticatedlibrary.model.api.v1.util.ModelData;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomParticleIcon {
    default ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData tileData) { return ModelData.EMPTY;}
    default TextureAtlasSprite getParticleIcon(ModelData data) {
        return ((BakedModel) this).getParticleIcon();
    }
}
