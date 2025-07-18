package com.github.salandora.sophisticatedlibrary.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FluidType implements FluidVariantAttributeHandler {
	private final int luminance;
	private final int density;
	private final int viscosity;

	public FluidType(final Properties properties) {
		this.luminance = properties.luminance;
		this.density = properties.density;
		this.viscosity = properties.viscosity;
	}

	@Override
	public int getLuminance(FluidVariant variant) {
		return this.getLuminance();
	}

	public int getLuminance() {
		return this.luminance;
	}

	public int getDensity() {
		return this.density;
	}

	@Override
	public int getViscosity(FluidVariant variant, @Nullable Level world) {
		return this.getViscosity();
	}

	public int getViscosity() {
		return this.viscosity;
	}

	public static final class Properties {
		private int luminance = 0;
		private int density = 1000;
		private int viscosity = 1000;

		private Properties() {}

		public static Properties create() {
			return new Properties();
		}

		public Properties lightLevel(int lightLevel) {
			if (lightLevel < 0 || lightLevel > 15)
				throw new IllegalArgumentException("The light level should be between [0,15].");
			this.luminance = lightLevel;
			return this;
		}

		public Properties density(int density) {
			this.density = density;
			return this;
		}

		public Properties viscosity(int viscosity) {
			if (viscosity < 0)
				throw new IllegalArgumentException("The viscosity should never be negative.");
			this.viscosity = viscosity;
			return this;
		}
	}
}
