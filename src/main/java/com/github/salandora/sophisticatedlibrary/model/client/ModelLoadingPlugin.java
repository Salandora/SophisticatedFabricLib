package com.github.salandora.sophisticatedlibrary.model.client;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.model.loading.IUnbakedGeometry;
import com.github.salandora.sophisticatedlibrary.model.loading.RegisterGeometryLoadersCallback;
import com.github.salandora.sophisticatedlibrary.model.mixin.client.accessors.BlockModelAccessor;
import com.github.salandora.sophisticatedlibrary.model.models.BlockModelWrapper;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.Context;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ModelLoadingPlugin implements PreparableModelLoadingPlugin<Map<ResourceLocation, IUnbakedGeometry>> {
	public static CompletableFuture<Map<ResourceLocation, IUnbakedGeometry>> MODEL_LOADER(ResourceManager resourceManager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> ModelBakery.MODEL_LISTER.listMatchingResources(resourceManager), executor).thenCompose(models -> {
			List<CompletableFuture<Pair<ResourceLocation, IUnbakedGeometry>>> list = Lists.newArrayList();
			for (Map.Entry<ResourceLocation, Resource> entry : models.entrySet()) {
				list.add(CompletableFuture.supplyAsync(() -> {
					try (Reader reader = entry.getValue().openAsReader()) {
						ResourceLocation id = ModelBakery.MODEL_LISTER.fileToId(entry.getKey());
						JsonObject element = JsonParser.parseReader(reader).getAsJsonObject();
						JsonElement loaderElement = element.get("loader");
						if (loaderElement == null) {
							return null;
						}

						ResourceLocation loaderLocation = ResourceLocation.parse(loaderElement.getAsString());
						var loader = RegisterGeometryLoadersCallback.get(loaderLocation);
						if (loader != null) {
							if (element.has("transform")) {
								SophisticatedLibrary.LOGGER.info("Found transform element in {}", entry.getKey());
							}

							if (element.has("visibility")) {
								SophisticatedLibrary.LOGGER.info("Found visibility element in {}", entry.getKey());
							}

							return Pair.of(id, loader.read(element));
						}
					} catch(IOException e) {
						SophisticatedLibrary.LOGGER.error("Failed to load model {}", entry.getKey(), e);
					}
					return null;
				}, executor));
			}

			return Util.sequence(list).thenApply((list2) -> list2.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
		});
	}

	@Override
	public void onInitializeModelLoader(Map<ResourceLocation, IUnbakedGeometry> data, Context context) {
		context.modifyModelBeforeBake().register((model, ctx) -> {
			if (model instanceof BlockModelWrapper) {
				return model;
			}

			if (model instanceof BlockModel blockModel && ((BlockModelAccessor) blockModel).parent() instanceof BlockModelWrapper wrapper) {
				// We need to replace the model of BlockModelWrapper wrapped models with the BlockModelWrapper model
				// or else they will not be visible
				return wrapper;
			}
			return model;
		});
		context.modifyModelOnLoad().register((model, ctx) -> {
			ResourceLocation id = ctx.resourceId();
			if (id != null) {
				var customModel = data.get(id);
				if (customModel != null && model instanceof BlockModel blockModel) {
					return new BlockModelWrapper(blockModel, customModel);
				}
			}

			return model;
		});
	}
}
