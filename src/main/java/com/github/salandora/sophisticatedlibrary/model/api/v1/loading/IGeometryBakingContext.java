package com.github.salandora.sophisticatedlibrary.model.api.v1.loading;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;

public interface IGeometryBakingContext {
	boolean useAmbientOcclusion();

	boolean isGui3d();

	boolean useBlockLight();

	ItemTransforms getTransforms();

	boolean hasTexture(String textureName);

	Material getMaterial(String name);

	Transformation getRootTransform();
}
