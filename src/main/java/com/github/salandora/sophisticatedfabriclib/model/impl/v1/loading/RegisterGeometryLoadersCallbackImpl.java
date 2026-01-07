package com.github.salandora.sophisticatedfabriclib.model.impl.v1.loading;

import com.github.salandora.sophisticatedfabriclib.model.api.v1.loading.IGeometryLoader;
import com.github.salandora.sophisticatedfabriclib.model.api.v1.loading.RegisterGeometryLoadersCallback;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class RegisterGeometryLoadersCallbackImpl {
	protected static final Map<ResourceLocation, IGeometryLoader<?>> LOADERS = Maps.newHashMap();

	public static void register(RegisterGeometryLoadersCallback.GeometryLoaderRegistry register) {
		register.registerLoader(LOADERS);
	}

	@Nullable
	public static IGeometryLoader<?> get(ResourceLocation loader) {
		return LOADERS.get(loader);
	}
}
