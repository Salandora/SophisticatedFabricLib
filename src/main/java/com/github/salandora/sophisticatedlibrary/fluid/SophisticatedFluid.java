package com.github.salandora.sophisticatedlibrary.fluid;

import com.github.salandora.sophisticatedlibrary.SophisticatedLibrary;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SophisticatedFluid implements ModInitializer {
	public static final ResourceKey<Registry<FluidType>> FLUID_TYPES_KEY = SophisticatedLibrary.key("fluid_types");

	public static final Registry<FluidType> FLUID_TYPES = FabricRegistryBuilder.createSimple(FLUID_TYPES_KEY).buildAndRegister();

	@Override
	public void onInitialize() {
	}
}
