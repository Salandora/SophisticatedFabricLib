package com.github.salandora.sophisticatedfabriclib;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedFabricLib implements ModInitializer {
    public static final String MOD_ID = "sophisticatedlibrary";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
    }

    public static ResourceLocation id(String regName) {
        return new ResourceLocation(MOD_ID, regName);
    }

    public static <T> ResourceKey<Registry<T>> key(String path) {
        return ResourceKey.createRegistryKey(id(path));
    }
}
