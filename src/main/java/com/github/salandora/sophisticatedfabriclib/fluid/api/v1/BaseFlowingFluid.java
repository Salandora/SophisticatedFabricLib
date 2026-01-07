package com.github.salandora.sophisticatedfabriclib.fluid.api.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class BaseFlowingFluid extends FlowingFluid {
	private final Supplier<? extends FluidType> fluidType;
	private final Supplier<? extends Fluid> flowing;
	private final Supplier<? extends Fluid> source;
	@Nullable
	private final Supplier<? extends Item> bucket;
	@Nullable
	private final Supplier<? extends LiquidBlock> block;
	private final int slopeFindDistance;
	private final int dropOff;
	private final float explosionResistance;
	private final int tickDelay;

	protected BaseFlowingFluid(Properties properties) {
		this.fluidType = properties.fluidType;
		this.flowing = properties.flowing;
		this.source = properties.source;
		this.bucket = properties.bucket;
		this.block = properties.block;
		this.slopeFindDistance = properties.slopeFindDistance;
		this.dropOff = properties.dropOff;
		this.explosionResistance = properties.explosionResistance;
		this.tickDelay = properties.tickDelay;
	}

	public FluidType getFluidType() {
		return this.fluidType.get();
	}

	@Override
	public Fluid getFlowing() {
		return flowing.get();
	}

	@Override
	public Fluid getSource() {
		return source.get();
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
		Block.dropResources(state, level, pos, blockEntity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return slopeFindDistance;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return dropOff;
	}

	@Override
	public Item getBucket() {
		return bucket != null ? bucket.get() : Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !isSame(fluid);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return tickDelay;
	}

	@Override
	protected float getExplosionResistance() {
		return explosionResistance;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		if (block != null)
			return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));

		return Blocks.AIR.defaultBlockState();
	}

	public static class Flowing extends BaseFlowingFluid {
		public Flowing(Properties properties) {
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends BaseFlowingFluid {
		public Source(Properties properties) {
			super(properties);
		}

		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Properties {
		private final Supplier<? extends FluidType> fluidType;
		private final Supplier<? extends Fluid> source;
		private final Supplier<? extends Fluid> flowing;
		private Supplier<? extends Item> bucket;
		private Supplier<? extends LiquidBlock> block;
		private int slopeFindDistance = 4;
		private int dropOff = 1;
		private float explosionResistance = 1;
		private int tickDelay = 5;

		public Properties(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> source, Supplier<? extends Fluid> flowing) {
			this.fluidType = fluidType;
			this.source = source;
			this.flowing = flowing;
		}

		public Properties bucket(Supplier<? extends Item> bucket) {
			this.bucket = bucket;
			return this;
		}

		public Properties block(Supplier<? extends LiquidBlock> block) {
			this.block = block;
			return this;
		}

		public Properties slopeFindDistance(int slopeFindDistance) {
			this.slopeFindDistance = slopeFindDistance;
			return this;
		}

		public Properties dropOff(int dropOff) {
			this.dropOff = dropOff;
			return this;
		}

		public Properties explosionResistance(float explosionResistance) {
			this.explosionResistance = explosionResistance;
			return this;
		}

		public Properties tickDelay(int tickDelay) {
			this.tickDelay = tickDelay;
			return this;
		}
	}
}
