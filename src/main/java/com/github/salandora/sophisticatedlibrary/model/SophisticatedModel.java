package com.github.salandora.sophisticatedlibrary.model;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import com.github.salandora.sophisticatedlibrary.model.loading.RegisterGeometryLoadersCallback;
import com.github.salandora.sophisticatedlibrary.model.models.DynamicFluidContainerModel;
import net.fabricmc.api.ModInitializer;

public class SophisticatedModel implements ModInitializer {
	@Override
	public void onInitialize() {
		RegisterGeometryLoadersCallback.register(loaders -> loaders.put(SophisticatedLibrary.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE));
	}
}
