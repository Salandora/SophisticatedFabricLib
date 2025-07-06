package com.github.salandora.sophisticatedlibrary.model.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public class SophisticatedModelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PreparableModelLoadingPlugin.register(ModelLoadingPlugin::MODEL_LOADER, new ModelLoadingPlugin());
    }
}
