package com.github.salandora.sophisticatedlibrary.model;

import com.github.salandora.sophisticatedlibrary.model.impl.v1.client.ModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public class SophisticatedModelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PreparableModelLoadingPlugin.register(ModelLoadingPlugin::MODEL_LOADER, new ModelLoadingPlugin());
    }
}
