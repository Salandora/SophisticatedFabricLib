package com.github.salandora.sophisticatedlibrary.model.api.v1.models;

import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.IGeometryBakingContext;
import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.IUnbakedGeometry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ElementsModel implements IUnbakedGeometry {
	private static final FaceBakery FACE_BAKERY = new FaceBakery();

	private final List<BlockElement> elements;

	public ElementsModel(List<BlockElement> elements) {
		this.elements = elements;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
		TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

		SimpleMeshBuilder builder = new SimpleMeshBuilder(RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(BlendMode.DEFAULT).find());

		for (BlockElement element : elements) {
			for (Map.Entry<Direction, BlockElementFace> entry : element.faces.entrySet()) {
				BlockElementFace face = entry.getValue();
				BakedQuad quad = bakeFace(element, face, spriteGetter.apply(context.getMaterial(face.texture)), entry.getKey(), modelState, modelLocation);

				if (face.cullForDirection == null) {
					builder.addUnculledFace(quad);
				} else {
					builder.addCulledFace(Direction.rotate(modelState.getRotation().getMatrix(), face.cullForDirection), quad);
				}
			}
		}

		return builder.buildBakedModel(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), particle, context.getTransforms(), overrides);
	}

	private static BakedQuad bakeFace(BlockElement element, BlockElementFace face, TextureAtlasSprite sprite, Direction facing, ModelState state, ResourceLocation location) {
		return FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, facing, state, element.rotation, element.shade, location);
	}
}