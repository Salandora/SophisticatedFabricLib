package com.github.salandora.sophisticatedfabriclib.tests.model;

import com.github.salandora.sophisticatedfabriclib.model.impl.v1.models.DynamicFluidContainerModel;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DynamicFluidContainerModelTest {
	@BeforeAll
	static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();

		if (!RendererAccess.INSTANCE.hasRenderer()) {
			MaterialFinder finder = mock(MaterialFinder.class);
			RenderMaterial material = mock(RenderMaterial.class);
			when(finder.blendMode(any())).thenReturn(finder);
			when(finder.emissive(anyBoolean())).thenReturn(finder);
			when(finder.find()).thenReturn(material);
			Renderer renderer = mock(Renderer.class);
			when(renderer.materialFinder()).thenReturn(finder);
			RendererAccess.INSTANCE.registerRenderer(renderer);
		}
	}

	@Test
	void loaderReadsFluidAndUsesLuminosityDefault() throws ReflectiveOperationException {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", BuiltInRegistries.FLUID.getKey(Fluids.WATER).toString());

		DynamicFluidContainerModel model = DynamicFluidContainerModel.Loader.INSTANCE.read(json);

		assertEquals(Fluids.WATER, fluidVariant(model).getFluid());
		assertTrue(applyFluidLuminosity(model));
	}

	@Test
	void loaderReadsExplicitLuminositySetting() throws ReflectiveOperationException {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", BuiltInRegistries.FLUID.getKey(Fluids.LAVA).toString());
		json.addProperty("apply_fluid_luminosity", false);

		DynamicFluidContainerModel model = DynamicFluidContainerModel.Loader.INSTANCE.read(json);

		assertEquals(Fluids.LAVA, fluidVariant(model).getFluid());
		assertFalse(applyFluidLuminosity(model));
	}

	@Test
	void loaderRejectsModelWithoutFluid() {
		RuntimeException exception = assertThrows(RuntimeException.class, () ->
				DynamicFluidContainerModel.Loader.INSTANCE.read(new JsonObject()));

		assertEquals("Bucket model requires 'fluid' value.", exception.getMessage());
	}

	@Test
	void recreatedEquivalentFluidVariantsHaveSameValueIdentity() {
		assertEquals(
				FluidVariant.of(Fluids.WATER),
				FluidVariant.of(Fluids.WATER)
		);
	}

	@Test
	void flowingFluidVariantsNormalizeToTheSameCacheIdentity() {
		assertEquals(FluidVariant.of(Fluids.WATER), FluidVariant.of(Fluids.FLOWING_WATER));
	}

	@Test
	void getFluidContainedFindsFluidInContainer() throws ReflectiveOperationException {
		Optional<ResourceAmount<FluidVariant>> contained = getFluidContained(new ItemStack(Items.WATER_BUCKET));

		assertTrue(contained.isPresent());
		assertEquals(FluidVariant.of(Fluids.WATER), contained.orElseThrow().resource());
	}

	@Test
	void getFluidContainedIgnoresEmptyAndNonFluidContainers() throws ReflectiveOperationException {
		assertTrue(getFluidContained(ItemStack.EMPTY).isEmpty());
		assertTrue(getFluidContained(new ItemStack(Items.DIRT)).isEmpty());
		assertTrue(getFluidContained(new ItemStack(Items.BUCKET)).isEmpty());
	}

	private static FluidVariant fluidVariant(DynamicFluidContainerModel model)
			throws ReflectiveOperationException {
		return (FluidVariant) field("fluidVariant").get(model);
	}

	private static boolean applyFluidLuminosity(DynamicFluidContainerModel model) throws ReflectiveOperationException {
		return (boolean) field("applyFluidLuminosity").get(model);
	}

	private static Field field(String name) throws ReflectiveOperationException {
		Field field = DynamicFluidContainerModel.class.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	@SuppressWarnings("unchecked")
	private static Optional<ResourceAmount<FluidVariant>> getFluidContained(ItemStack stack)
			throws ReflectiveOperationException {
		Method method = DynamicFluidContainerModel.class.getDeclaredMethod("getFluidContained", ItemStack.class);
		method.setAccessible(true);
		return (Optional<ResourceAmount<FluidVariant>>) method.invoke(null, stack);
	}
}
