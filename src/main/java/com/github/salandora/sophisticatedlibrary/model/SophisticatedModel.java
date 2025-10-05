package com.github.salandora.sophisticatedlibrary.model;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.model.api.v1.loading.RegisterGeometryLoadersCallback;
import com.github.salandora.sophisticatedlibrary.model.impl.v1.models.DynamicFluidContainerModel;
import net.fabricmc.api.ModInitializer;

public class SophisticatedModel implements ModInitializer {
	@Override
	public void onInitialize() {
		RegisterGeometryLoadersCallback.register(loaders -> loaders.put(SophisticatedLibrary.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE));
	}
}
