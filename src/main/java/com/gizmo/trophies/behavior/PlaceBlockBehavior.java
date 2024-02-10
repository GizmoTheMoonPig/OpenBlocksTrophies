package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Locale;

public record PlaceBlockBehavior(BlockState placedBlock, PlacementMethod placement, int cooldown) implements CustomBehavior {

	public static final Codec<PlaceBlockBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockState.CODEC.fieldOf("block").forGetter(PlaceBlockBehavior::placedBlock),
			PlacementMethod.CODEC.fieldOf("method").forGetter(PlaceBlockBehavior::placement),
			Codec.INT.optionalFieldOf("cooldown", 100).forGetter(PlaceBlockBehavior::cooldown)
	).apply(instance, PlaceBlockBehavior::new));

	public PlaceBlockBehavior(Block block, PlacementMethod placement) {
		this(block.defaultBlockState(), placement, 100);
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.PLACE_BLOCK.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos base = block.getBlockPos();
		Level level = block.getLevel();

		if (level != null) {
			switch (this.placement()) {
				case ABOVE -> this.placeBlock(level, base.above());
				case CROSS -> {
					for (Direction direction : Direction.Plane.HORIZONTAL) {
						this.placeBlock(level, base.relative(direction));
					}
				}
				case AROUND -> {
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							this.placeBlock(level, base.offset(x, 0, z));
						}
					}
				}
				case ENCASE -> {
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <= 1; y++) {
							for (int z = -1; z <= 1; z++) {
								this.placeBlock(level, base.offset(x, y, z));
							}
						}
					}
				}
			}
		}

		return this.cooldown();
	}

	private void placeBlock(Level level, BlockPos pos) {
		if (level.getBlockState(pos).canBeReplaced() && this.placedBlock().canSurvive(level, pos)) {
			if (this.placedBlock().getBlock() instanceof SnowLayerBlock layer) {
				int layers = level.getRandom().nextInt(8) + 1;
				level.setBlockAndUpdate(pos, layer.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers));
			} else if (this.placedBlock().getBlock() instanceof LiquidBlock liquid && liquid.getFluid().getFluidType().isVaporizedOnPlacement(level, pos, new FluidStack(liquid.getFluid(), FluidType.BUCKET_VOLUME))) {
				level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
			} else {
				level.setBlockAndUpdate(pos, this.placedBlock());
			}
		}
	}

	public enum PlacementMethod implements StringRepresentable {
		ABOVE,
		CROSS,
		AROUND,
		ENCASE;

		public static final EnumCodec<PlacementMethod> CODEC = StringRepresentable.fromEnum(PlacementMethod::values);

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
