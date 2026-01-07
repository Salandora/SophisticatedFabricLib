package com.github.salandora.sophisticatedfabriclib.fluid;

import com.github.salandora.sophisticatedfabriclib.SophisticatedFabricLib;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SophisticatedFluid implements ModInitializer {
	public static final ResourceKey<Registry<FluidType>> FLUID_TYPES_KEY = SophisticatedFabricLib.key("fluid_types");

	public static final Registry<FluidType> FLUID_TYPES = FabricRegistryBuilder.createSimple(FLUID_TYPES_KEY).buildAndRegister();

	@Override
	public void onInitialize() {
	}
}
