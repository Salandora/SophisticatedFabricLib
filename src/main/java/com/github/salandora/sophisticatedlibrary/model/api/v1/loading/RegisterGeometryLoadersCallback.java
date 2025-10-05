package com.github.salandora.sophisticatedlibrary.model.api.v1.loading;

import com.github.salandora.sophisticatedlibrary.model.impl.v1.loading.RegisterGeometryLoadersCallbackImpl;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public interface RegisterGeometryLoadersCallback {
	static void register(GeometryLoaderRegistry register) {
		RegisterGeometryLoadersCallbackImpl.register(register);
	}

	@Nullable
	static IGeometryLoader<?> get(ResourceLocation loader) {
		return RegisterGeometryLoadersCallbackImpl.get(loader);
	}

	@FunctionalInterface
	interface GeometryLoaderRegistry {
		void registerLoader(Map<ResourceLocation, IGeometryLoader<?>> loaders);
	}
}
