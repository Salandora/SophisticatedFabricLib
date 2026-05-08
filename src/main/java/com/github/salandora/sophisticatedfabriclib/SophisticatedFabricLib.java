package com.github.salandora.sophisticatedfabriclib;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedFabricLib {
    public static final String MOD_ID = "sophisticatedfabriclib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String regName) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, regName);
    }

    public static <T> ResourceKey<Registry<T>> key(String path) {
        return ResourceKey.createRegistryKey(id(path));
    }
}
