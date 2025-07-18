package com.github.salandora.sophisticatedlibrary.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class FluidStack extends SingleFluidStorage {
	public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

	public static final Codec<FluidStack> CODEC = Codec.lazyInitialized(() ->
			RecordCodecBuilder.create(
			instance -> instance.group(
					FluidVariant.CODEC.fieldOf("FluidVariant").forGetter(FluidStack::getVariant),
					Codec.LONG.fieldOf("Amount").forGetter(FluidStack::getAmount)
			).apply(instance, FluidStack::new)
	));

	public static final Codec<FluidStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
			.xmap(optional -> optional.orElse(FluidStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));


	public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = StreamCodec.composite(
			FluidVariant.PACKET_CODEC, FluidStack::getVariant,
			ByteBufCodecs.VAR_LONG, FluidStack::getAmount,
			FluidStack::new
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> OPTIONAL_STREAM_CODEC = STREAM_CODEC
			.apply(ByteBufCodecs::optional)
			.map(optional -> optional.orElse(FluidStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));

	public static boolean isSameFluidSameComponents(FluidStack first, FluidStack second) {
		return first.isResourceBlank() && second.isResourceBlank() || first.variant == second.variant;
	}

	public FluidStack(StorageView<FluidVariant> view) {
		this(view.getResource(), view.getAmount());
	}

	public FluidStack(ResourceAmount<FluidVariant> resource) {
		this(resource.resource(), resource.amount());
	}

	public FluidStack(Fluid fluid, long amount) {
		this(FluidVariant.of(fluid), amount);
	}

	public FluidStack(FluidVariant variant, long amount) {
		this.variant = variant;
		this.amount = amount;
	}

	protected FluidStack() {
	}

	public Component getHoverName() {
		return FluidVariantAttributes.getName(this.variant);
	}

	@Override
	protected long getCapacity(FluidVariant variant) {
		return Long.MAX_VALUE;
	}

	public Fluid getFluid() {
		return this.variant.getFluid();
	}

	public FluidVariant getVariant() {
		return this.variant;
	}

	public boolean isEmpty() {
		return this.isResourceBlank();
	}

	public FluidStack copy() {
		return new FluidStack(this.variant, this.amount);
	}

	public CompoundTag saveOptional(HolderLookup.Provider lookup) {
		if (this.isEmpty()) {
			return new CompoundTag();
		}

		CompoundTag tag = new CompoundTag();
		writeNbt(tag, lookup);
		return tag;
	}

	public static FluidStack parseOptional(HolderLookup.Provider lookup, CompoundTag tag) {
		if (tag.isEmpty()) {
			return EMPTY;
		}
		FluidStack stack = new FluidStack();
		stack.readNbt(tag, lookup);
		return stack;
	}

	public boolean is(TagKey<Fluid> tag) {
		return this.variant.getFluid().is(tag);
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public void grow(long added) {
		this.setAmount(getAmount() + added);
	}

	public void shrink(long removed) {
		this.grow(-removed);
	}
}
