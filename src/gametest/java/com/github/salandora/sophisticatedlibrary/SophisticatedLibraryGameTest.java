package com.github.salandora.sophisticatedlibrary;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class SophisticatedLibraryGameTest implements FabricGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void test(GameTestHelper context) {
		context.succeed();
	}
}
