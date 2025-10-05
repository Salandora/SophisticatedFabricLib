package com.github.salandora.sophisticatedlibrary.common.mixin.common;

import com.github.salandora.sophisticatedlibrary.common.extensions.entity.SophisticatedShearable;
import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ DeadBushBlock.class, LeavesBlock.class, SeagrassBlock.class, TallGrassBlock.class, VineBlock.class, WebBlock.class })
public class ShearableBlocksMixin_Shearable implements SophisticatedShearable {
}
