package com.github.salandora.sophisticatedlibrary.model.api.v1.models;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

public class SimpleMeshBuilder {
	private final MeshBuilder builder;
	private final RenderMaterial material;

	public SimpleMeshBuilder(RenderMaterial material) {
		this.builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		this.material = material;
	}

	public SimpleMeshBuilder addCulledFace(Direction facing, BakedQuad quad) {
		builder.getEmitter().fromVanilla(quad, material, facing).emit();
		return this;
	}

	public SimpleMeshBuilder addUnculledFace(BakedQuad quad) {
		builder.getEmitter().fromVanilla(quad, material, null).emit();
		return this;
	}

	public Mesh build() {
		return builder.build();
	}

	public BakedModel buildBakedModel(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides) {
		return new MeshBakedModel(builder.build(), hasAmbientOcclusion, usesBlockLight, isGui3d, particle, transforms, overrides);
	}
}
