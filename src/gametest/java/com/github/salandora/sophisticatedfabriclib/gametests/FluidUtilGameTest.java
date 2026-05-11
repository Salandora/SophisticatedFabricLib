package com.github.salandora.sophisticatedfabriclib.gametests;

import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.BucketPickupHandlerWrapper;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidStack;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.FluidUtil;
import com.github.salandora.sophisticatedfabriclib.fluid.api.v1.IFluidHandler;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class FluidUtilGameTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidPlacesWaterInWorld(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = new SingleFluidSource(new FluidStack(Fluids.WATER, FluidConstants.BUCKET));

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, new FluidStack(Fluids.WATER, FluidConstants.BUCKET));

		context.assertTrue(placed, "Expected water to be placed");
		context.assertBlockPresent(Blocks.WATER, pos);
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenPositionIsNotLoaded(GameTestHelper context) {
		SingleFluidSource source = waterSource();
		BlockPos unloadedPos = new BlockPos(30_000_000, 100, 30_000_000);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, unloadedPos, source, waterStack());

		context.assertFalse(placed, "Expected placement in an unloaded position to fail");
		context.assertFalse(source.getFluidInTank(0).isEmpty(), "Expected source not to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenFluidIsEmpty(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = waterSource();

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, new FluidStack(Fluids.EMPTY, FluidConstants.BUCKET));

		context.assertFalse(placed, "Expected empty fluid placement to fail");
		context.assertBlockPresent(Blocks.AIR, pos);
		context.assertFalse(source.getFluidInTank(0).isEmpty(), "Expected source not to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenBlockCannotBeReplaced(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = waterSource();
		context.setBlock(pos, Blocks.STONE);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, waterStack());

		context.assertFalse(placed, "Expected placement into stone to fail");
		context.assertBlockPresent(Blocks.STONE, pos);
		context.assertFalse(source.getFluidInTank(0).isEmpty(), "Expected source not to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenLavaTargetsWaterloggableBlock(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = new SingleFluidSource(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET));
		context.setBlock(pos, Blocks.OAK_SLAB);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, new FluidStack(Fluids.LAVA, FluidConstants.BUCKET));

		context.assertFalse(placed, "Expected lava placement into a waterloggable block to fail");
		context.assertBlockPresent(Blocks.OAK_SLAB, pos);
		context.assertFalse(source.getFluidInTank(0).isEmpty(), "Expected source not to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenExistingFluidDiffers(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = new SingleFluidSource(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET));
		context.setBlock(pos, Blocks.WATER);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, new FluidStack(Fluids.LAVA, FluidConstants.BUCKET));

		context.assertFalse(placed, "Expected lava placement into water to fail");
		context.assertBlockPresent(Blocks.WATER, pos);
		context.assertFalse(source.getFluidInTank(0).isEmpty(), "Expected source not to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidFailsWhenSourceCannotDrain(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = new SingleFluidSource(FluidStack.EMPTY);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, waterStack());

		context.assertFalse(placed, "Expected placement with an empty source to fail");
		context.assertBlockPresent(Blocks.AIR, pos);
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidWaterlogsWaterloggableBlock(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = waterSource();
		context.setBlock(pos, Blocks.OAK_SLAB);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, waterStack());

		context.assertTrue(placed, "Expected water placement into slab to succeed");
		BlockState state = context.getBlockState(pos);
		context.assertTrue(state.is(Blocks.OAK_SLAB), "Expected slab to remain in place");
		context.assertTrue(state.getValue(WATERLOGGED), "Expected slab to be waterlogged");
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidReplacesReplaceableBlock(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = waterSource();
		context.setBlock(pos, Blocks.SHORT_GRASS);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, waterStack());

		context.assertTrue(placed, "Expected water placement into short grass to succeed");
		context.assertBlockPresent(Blocks.WATER, pos);
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidSucceedsWhenSameFluidSourceAlreadyExists(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = waterSource();
		context.setBlock(pos, Blocks.WATER);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, waterStack());

		context.assertTrue(placed, "Expected placing water into an existing water source to succeed");
		context.assertBlockPresent(Blocks.WATER, pos);
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidSucceedsWhenSameLavaSourceAlreadyExists(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		SingleFluidSource source = new SingleFluidSource(lavaStack());
		context.setBlock(pos, Blocks.LAVA);

		boolean placed = FluidUtil.tryPlaceFluid(null, context.getLevel(), InteractionHand.MAIN_HAND, context.absolutePos(pos), source, lavaStack());

		context.assertTrue(placed, "Expected placing lava into an existing lava source to succeed");
		context.assertBlockPresent(Blocks.LAVA, pos);
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidEvaporatesWaterInUltraWarmDimension(GameTestHelper context) {
		ServerLevel nether = context.getLevel().getServer().getLevel(Level.NETHER);
		BlockPos pos = new BlockPos(0, 80, 0);
		SingleFluidSource source = waterSource();

		context.assertTrue(nether != null, "Expected Nether level to be available");
		nether.getChunkAt(pos);
		nether.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

		boolean placed = FluidUtil.tryPlaceFluid(null, nether, InteractionHand.MAIN_HAND, pos, source, waterStack());

		context.assertTrue(placed, "Expected water placement in the Nether to report success");
		context.assertFalse(nether.getBlockState(pos).is(Blocks.WATER), "Expected water not to be placed in the Nether");
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void tryPlaceFluidPlacesLavaInUltraWarmDimension(GameTestHelper context) {
		ServerLevel nether = context.getLevel().getServer().getLevel(Level.NETHER);
		BlockPos pos = new BlockPos(0, 80, 1);
		SingleFluidSource source = new SingleFluidSource(lavaStack());

		context.assertTrue(nether != null, "Expected Nether level to be available");
		nether.getChunkAt(pos);
		nether.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

		boolean placed = FluidUtil.tryPlaceFluid(null, nether, InteractionHand.MAIN_HAND, pos, source, lavaStack());

		context.assertTrue(placed, "Expected lava placement in the Nether to succeed");
		context.assertTrue(nether.getBlockState(pos).is(Blocks.LAVA), "Expected lava to be placed in the Nether");
		context.assertTrue(source.getFluidInTank(0).isEmpty(), "Expected source to be drained");
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void bucketPickupWrapperExposesSourceFluidAndRejectsFill(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		context.setBlock(pos, Blocks.WATER);

		BucketPickupHandlerWrapper wrapper = bucketPickupWrapper(context, pos);

		context.assertTrue(wrapper.getTanks() == 1, "Expected one tank");
		context.assertTrue(wrapper.getTankCapacity(0) == FluidConstants.BUCKET, "Expected bucket capacity");
		context.assertTrue(wrapper.getFluidInTank(0).getFluid() == Fluids.WATER, "Expected water in tank");
		context.assertTrue(wrapper.getFluidInTank(0).getAmount() == FluidConstants.BUCKET, "Expected one bucket of water");
		context.assertTrue(wrapper.isFluidValid(0, lavaStack()), "Expected wrapper to accept any fluid as valid");
		context.assertTrue(wrapper.fill(waterStack(), IFluidHandler.FluidAction.EXECUTE) == 0, "Expected fill to be unsupported");
		wrapper.setFluidInTank(0, lavaStack());
		context.assertTrue(wrapper.getFluidInTank(0).getFluid() == Fluids.WATER, "Expected setFluidInTank to be a noop");
		context.assertBlockPresent(Blocks.WATER, pos);
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void bucketPickupWrapperDrainsByAmount(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		context.setBlock(pos, Blocks.WATER);
		BucketPickupHandlerWrapper wrapper = bucketPickupWrapper(context, pos);

		FluidStack tooSmall = wrapper.drain(FluidConstants.BUCKET - 1, IFluidHandler.FluidAction.EXECUTE);
		context.assertTrue(tooSmall.isEmpty(), "Expected less than a bucket to drain nothing");
		context.assertBlockPresent(Blocks.WATER, pos);

		FluidStack simulated = wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.SIMULATE);
		context.assertTrue(simulated.getFluid() == Fluids.WATER, "Expected simulated drain to return water");
		context.assertTrue(simulated.getAmount() == FluidConstants.BUCKET, "Expected simulated drain to return one bucket");
		context.assertBlockPresent(Blocks.WATER, pos);

		FluidStack drained = wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE);
		context.assertTrue(drained.getFluid() == Fluids.WATER, "Expected executed drain to return water");
		context.assertTrue(drained.getAmount() == FluidConstants.BUCKET, "Expected executed drain to return one bucket");
		context.assertBlockPresent(Blocks.AIR, pos);
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void bucketPickupWrapperDrainsByMatchingResource(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		context.setBlock(pos, Blocks.WATER);
		BucketPickupHandlerWrapper wrapper = bucketPickupWrapper(context, pos);

		context.assertTrue(wrapper.drain(FluidStack.EMPTY, IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Expected empty resource to drain nothing");
		context.assertTrue(wrapper.drain(new FluidStack(Fluids.WATER, FluidConstants.BUCKET - 1), IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Expected partial bucket resource to drain nothing");
		context.assertTrue(wrapper.drain(lavaStack(), IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Expected mismatched resource to drain nothing");
		context.assertBlockPresent(Blocks.WATER, pos);

		FluidStack simulated = wrapper.drain(waterStack(), IFluidHandler.FluidAction.SIMULATE);
		context.assertTrue(simulated.getFluid() == Fluids.WATER, "Expected simulated matching resource drain to return water");
		context.assertTrue(simulated.getAmount() == FluidConstants.BUCKET, "Expected simulated matching resource drain to return one bucket");
		context.assertBlockPresent(Blocks.WATER, pos);

		FluidStack drained = wrapper.drain(waterStack(), IFluidHandler.FluidAction.EXECUTE);
		context.assertTrue(drained.getFluid() == Fluids.WATER, "Expected executed matching resource drain to return water");
		context.assertTrue(drained.getAmount() == FluidConstants.BUCKET, "Expected executed matching resource drain to return one bucket");
		context.assertBlockPresent(Blocks.AIR, pos);
		context.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void bucketPickupWrapperReportsEmptyWhenNoFluidIsPresent(GameTestHelper context) {
		BlockPos pos = new BlockPos(1, 1, 1);
		context.setBlock(pos, Blocks.AIR);
		BucketPickupHandlerWrapper wrapper = bucketPickupWrapper(context, pos);

		context.assertTrue(wrapper.getFluidInTank(0).isEmpty(), "Expected empty tank");
		context.assertTrue(wrapper.drain(FluidConstants.BUCKET, IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Expected amount drain to return empty");
		context.assertTrue(wrapper.drain(waterStack(), IFluidHandler.FluidAction.EXECUTE).isEmpty(), "Expected resource drain to return empty");
		context.assertBlockPresent(Blocks.AIR, pos);
		context.succeed();
	}

	private static BucketPickupHandlerWrapper bucketPickupWrapper(GameTestHelper context, BlockPos pos) {
		return new BucketPickupHandlerWrapper(null, (BucketPickup) Blocks.WATER, context.getLevel(), context.absolutePos(pos));
	}

	private static FluidStack waterStack() {
		return new FluidStack(Fluids.WATER, FluidConstants.BUCKET);
	}

	private static FluidStack lavaStack() {
		return new FluidStack(Fluids.LAVA, FluidConstants.BUCKET);
	}

	private static SingleFluidSource waterSource() {
		return new SingleFluidSource(waterStack());
	}

	private static final class SingleFluidSource implements IFluidHandler {
		private FluidStack content;

		private SingleFluidSource(FluidStack content) {
			this.content = content;
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			return content;
		}

		@Override
		public void setFluidInTank(int tank, FluidStack fluidStack) {
			content = fluidStack;
		}

		@Override
		public long getTankCapacity(int tank) {
			return FluidConstants.BUCKET;
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack fluidStack) {
			return FluidStack.isSameFluidSameComponents(content, fluidStack);
		}

		@Override
		public long fill(FluidStack fluidStack, FluidAction action) {
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack drainStack, FluidAction action) {
			if (content.isEmpty() || !FluidStack.isSameFluidSameComponents(content, drainStack)) {
				return FluidStack.EMPTY;
			}

			long drained = Math.min(content.getAmount(), drainStack.getAmount());
			FluidStack result = content.copyWithAmount(drained);
			if (action.execute()) {
				content.shrink(drained);
				if (content.getAmount() <= 0) {
					content = FluidStack.EMPTY;
				}
			}
			return result;
		}

		@Override
		public FluidStack drain(long maxDrain, FluidAction action) {
			return drain(content.copyWithAmount(Math.min(content.getAmount(), maxDrain)), action);
		}
	}
}
