package com.github.salandora.sophisticatedfabriclib.model;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.model.api.v1.loading.RegisterGeometryLoadersCallback;
import com.github.salandora.sophisticatedfabriclib.model.impl.v1.models.DynamicFluidContainerModel;
import net.fabricmc.api.ModInitializer;

public class SophisticatedModel implements ModInitializer {
	@Override
	public void onInitialize() {
		RegisterGeometryLoadersCallback.register(loaders -> loaders.put(SophisticatedFabricLib.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE));
	}
}
