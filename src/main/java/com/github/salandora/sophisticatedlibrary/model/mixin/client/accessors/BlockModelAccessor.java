package com.github.salandora.sophisticatedlibrary.model.mixin.client.accessors;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {
	@Accessor("parent")
	BlockModel parent();

	@Accessor("parentLocation")
	ResourceLocation parentLocation();

	@Accessor("textureMap")
	Map<String, Either<Material, String>> textureMap();

	@Invoker("bakeFace")
	static BakedQuad callBakeFace(BlockElement element, BlockElementFace face, TextureAtlasSprite sprite, Direction facing, ModelState state) {
		throw new UnsupportedOperationException();
	}
}
