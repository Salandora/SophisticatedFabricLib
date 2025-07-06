package com.github.salandora.sophisticatedlibrary.model.models;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class MeshBakedModel implements BakedModel {
	private final Mesh mesh;
	private List<BakedQuad>[] quads;
	private final boolean hasAmbientOcclusion;
	private final boolean usesBlockLight;
	private final boolean isGui3d;
	private final TextureAtlasSprite particle;
	private final ItemTransforms transforms;
	private final ItemOverrides overrides;

	public MeshBakedModel(Mesh mesh, boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides) {
		this.mesh = mesh;
		this.hasAmbientOcclusion = hasAmbientOcclusion;
		this.usesBlockLight = usesBlockLight;
		this.isGui3d = isGui3d;
		this.particle = particle;
		this.transforms = transforms;
		this.overrides = overrides;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		this.mesh.outputTo(context.getEmitter());
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		this.mesh.outputTo(context.getEmitter());
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
		if (quads == null) {
			quads = ModelHelper.toQuadLists(mesh);
		}
		return quads[direction == null ? ModelHelper.NULL_FACE_ID : direction.get3DDataValue()];
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return hasAmbientOcclusion;
	}

	@Override
	public boolean isGui3d() {
		return isGui3d;
	}

	@Override
	public boolean usesBlockLight() {
		return usesBlockLight;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return particle;
	}

	@Override
	public ItemTransforms getTransforms() {
		return transforms;
	}

	@Override
	public ItemOverrides getOverrides() {
		return overrides;
	}
}
