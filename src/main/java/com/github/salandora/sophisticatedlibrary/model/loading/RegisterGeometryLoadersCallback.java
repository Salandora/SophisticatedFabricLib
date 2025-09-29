package com.github.salandora.sophisticatedlibrary.model.loading;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class RegisterGeometryLoadersCallback {
	protected static final Map<ResourceLocation, IGeometryLoader<?>> LOADERS = Maps.newHashMap();

	public static void register(GeometryLoaderRegistry register) {
		register.registerLoader(LOADERS);
	}

	@Nullable
	public static IGeometryLoader<?> get(ResourceLocation loader) {
		return LOADERS.get(loader);
	}

	@FunctionalInterface
	public interface GeometryLoaderRegistry {
		void registerLoader(Map<ResourceLocation, IGeometryLoader<?>> loaders);
	}
}
