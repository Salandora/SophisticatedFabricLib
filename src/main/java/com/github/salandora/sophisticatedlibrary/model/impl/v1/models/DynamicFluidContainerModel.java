package com.github.salandora.sophisticatedlibrary.model.impl.v1.models;

import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.GeometryBakingContext;
import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.IGeometryBakingContext;
import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.IGeometryLoader;
import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.IUnbakedGeometry;
import com.github.salandora.sophisticatedlibrary.model.api.v1.models.SimpleMeshBuilder;
import com.github.salandora.sophisticatedlibrary.model.api.v1.util.SimpleModelState;
import com.github.salandora.sophisticatedlibrary.model.api.v1.util.UnbakedGeometryHelper;
import com.github.salandora.sophisticatedlibrary.util.Lazy;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicFluidContainerModel implements IUnbakedGeometry {
	private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());

	private final FluidVariant fluidVariant;
	private final boolean applyFluidLuminosity;

	private final RenderMaterial translucentMaterial;
	private final RenderMaterial translucentEmissiveMaterial;

	private DynamicFluidContainerModel(FluidVariant fluid, boolean applyFluidLuminosity) {
		this.fluidVariant = fluid;
		this.applyFluidLuminosity = applyFluidLuminosity;

		var finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
		this.translucentMaterial = finder.blendMode(BlendMode.TRANSLUCENT).find();
		finder.clear();
		this.translucentEmissiveMaterial = finder.blendMode(BlendMode.TRANSLUCENT).emissive(true).find();
	}

	public DynamicFluidContainerModel withFluid(Fluid newFluid) {
		return new DynamicFluidContainerModel(FluidVariant.of(newFluid), applyFluidLuminosity);
	}

	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		return new LazyBaked(() -> {
			Material particleLocation = context.hasTexture("particle") ? context.getMaterial("particle") : null;
			Material baseLocation = context.hasTexture("base") ? context.getMaterial("base") : null;
			Material fluidMaskLocation = context.hasTexture("fluid") ? context.getMaterial("fluid") : null;

			TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
			TextureAtlasSprite templateSprite = fluidMaskLocation != null ? spriteGetter.apply(fluidMaskLocation) : null;
			TextureAtlasSprite fluidSprite = !fluidVariant.isBlank()? FluidVariantRendering.getSprite(fluidVariant) : null;

			TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;
			if (particleSprite == null) particleSprite = fluidSprite;
			if (particleSprite == null) particleSprite = baseSprite;

			var itemContext = GeometryBakingContext.builder(context)
					.withGui3d(false)
					.withUseBlockLight(false)
					.build();
			var overrideHandler = new ContainedFluidOverrideHandler(overrides, baker, itemContext, this);

			Mesh baseMesh = null, fluidMesh = null;
			if (baseSprite != null) {
				// Base texture
				var builder = new SimpleMeshBuilder(translucentMaterial);
				var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.contents());
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelState);

				quads.forEach(builder::addUnculledFace);

				baseMesh = builder.build();
			}

			if (fluidSprite != null) {
				// Fluid layer
				var builder = new SimpleMeshBuilder(applyFluidLuminosity && FluidVariantAttributes.getLuminance(fluidVariant) > 0 ? translucentEmissiveMaterial : translucentMaterial);

				var transformedState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
				var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite.contents()); // Use template as mask
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState); // Bake with fluid texture

				quads.forEach(builder::addUnculledFace);

				fluidMesh = builder.build();
			}

			return new Baked(itemContext, particleSprite, overrideHandler, baseMesh, fluidMesh);
		});
	}

	private static final class LazyBaked implements BakedModel, WrapperBakedModel {
		private final Lazy<BakedModel> lazyWrappedModel;

		private LazyBaked(Supplier<BakedModel> lazyWrappedModel) {
			this.lazyWrappedModel = Lazy.of(lazyWrappedModel);
		}

		@Override
		public boolean isVanillaAdapter() {
			return getWrappedModel().isVanillaAdapter();
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			getWrappedModel().emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
			getWrappedModel().emitItemQuads(stack, randomSupplier, context);
		}

		@Override
		public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource rand) {
			return getWrappedModel().getQuads(blockState, face, rand);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return getWrappedModel().useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return getWrappedModel().isGui3d();
		}

		@Override
		public boolean isCustomRenderer() {
			return getWrappedModel().isCustomRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return getWrappedModel().getParticleIcon();
		}

		@Override
		public boolean usesBlockLight() {
			return getWrappedModel().usesBlockLight();
		}

		@Override
		public ItemTransforms getTransforms() {
			return getWrappedModel().getTransforms();
		}

		@Override
		public ItemOverrides getOverrides() {
			return getWrappedModel().getOverrides();
		}

		@Override
		public BakedModel getWrappedModel() {
			return lazyWrappedModel.get();
		}
	}

	private static final class Baked implements BakedModel {
		private final IGeometryBakingContext itemContext;
		private final TextureAtlasSprite particleSprite;
		private final ItemOverrides overrides;

		private final Mesh baseMesh;
		private final Mesh fluidMesh;

		private Baked(IGeometryBakingContext itemContext, TextureAtlasSprite particleSprite, ItemOverrides overrides, Mesh baseMesh, Mesh fluidMesh) {
			this.itemContext = itemContext;
			this.particleSprite = particleSprite;
			this.overrides = overrides;

			this.baseMesh = baseMesh;
			this.fluidMesh = fluidMesh;
		}

		@Override
		public boolean isVanillaAdapter() {
			// Need to use fabrics rendering api because with the getQuads function water textures still break the rendering
			return false;
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
			baseMesh.outputTo(context.getEmitter());
			fluidMesh.outputTo(context.getEmitter());
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
			return List.of();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return itemContext.useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return itemContext.isGui3d();
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return particleSprite;
		}

		@Override
		public boolean usesBlockLight() {
			return itemContext.useBlockLight();
		}

		@Override
		public ItemTransforms getTransforms() {
			return itemContext.getTransforms();
		}

		@Override
		public ItemOverrides getOverrides() {
			return overrides;
		}
	}

	public static final class Loader implements IGeometryLoader<DynamicFluidContainerModel> {
		public static final Loader INSTANCE = new Loader();

		private Loader() {}

		@Override
		public DynamicFluidContainerModel read(JsonObject jsonObject) {
			if (!jsonObject.has("fluid"))
				throw new RuntimeException("Bucket model requires 'fluid' value.");

			FluidVariant variant;
			if (jsonObject.has("fluid")) {
				ResourceLocation fluidName = ResourceLocation.parse(jsonObject.get("fluid").getAsString());

				Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);
				variant = FluidVariant.of(fluid);
			} else if (jsonObject.has("variant")) {
				variant = FluidVariant.CODEC.decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst();
			} else {
				throw new RuntimeException("Either 'fluid' or ' variant' must be present for a dynamic fluid container model");
			}

			boolean applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "apply_fluid_luminosity", true);

			// create new model with correct liquid
			return new DynamicFluidContainerModel(variant, applyFluidLuminosity);
		}
	}

	private static final class ContainedFluidOverrideHandler extends ItemOverrides {
		private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
		private final ItemOverrides nested;
		private final ModelBaker baker;
		private final IGeometryBakingContext owner;
		private final DynamicFluidContainerModel parent;

		private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, DynamicFluidContainerModel parent) {
			this.nested = nested;
			this.baker = baker;
			this.owner = owner;
			this.parent = parent;
		}

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
			if (overridden != originalModel) return overridden;
			return getFluidContained(stack)
					.map(f -> {
						Fluid fluid = f.resource().getFluid();
						String name = BuiltInRegistries.FLUID.getKey(fluid).toString();

						if (!cache.containsKey(name)) {
							DynamicFluidContainerModel unbaked = this.parent.withFluid(fluid);
							BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
							cache.put(name, bakedModel);
							return bakedModel;
						}

						return cache.get(name);
					})
					// not a fluid item apparently
					.orElse(originalModel); // empty bucket
		}

		/**
		 * Helper method to get the fluid contained in an itemStack
		 */
		public static Optional<ResourceAmount<FluidVariant>> getFluidContained(ItemStack container) {
			if (!container.isEmpty()) {
				container = container.copyWithCount(1);
				Optional<ResourceAmount<FluidVariant>> fluidContained =
						Optional.ofNullable(
								ContainerItemContext.withConstant(container).find(FluidStorage.ITEM)
						).map(handler ->
								StorageUtil.findExtractableContent(handler, null)
						);

				return fluidContained.filter(f -> !f.resource().isBlank());
			}
			return Optional.empty();
		}
	}
}
