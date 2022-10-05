package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public class PlaceBlockBehavior extends CustomBehavior {

	private Block blockToPlace;
	private boolean aroundTrophy;

	public PlaceBlockBehavior() {
	}

	public PlaceBlockBehavior(Block block, boolean placeAroundTrophy) {
		this.blockToPlace = block;
		this.aroundTrophy = placeAroundTrophy;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("place_block");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("block", context.serialize(ForgeRegistries.BLOCKS.getKey(this.blockToPlace).toString()));
		object.add("place_around_trophy", context.serialize(this.aroundTrophy));
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(object, "block")));
		boolean placeAround = GsonHelper.getAsBoolean(object, "place_around_trophy");
		return new PlaceBlockBehavior(block, placeAround);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		BlockPos base = block.getBlockPos();
		Level level = block.getLevel();
		assert level != null;

		if (this.aroundTrophy) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos pos = base.offset(x, 0, z);
					if (level.getBlockState(pos).isAir() && this.blockToPlace.canSurvive(this.blockToPlace.defaultBlockState(), level, pos)) {
						if (this.blockToPlace instanceof SnowLayerBlock layer) {
							int layers = level.getRandom().nextInt(8) + 1;
							level.setBlockAndUpdate(pos, layer.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers));
						} else {
							if (this.blockToPlace instanceof LiquidBlock liquid && liquid.getFluid() == Fluids.WATER && player.getLevel().dimensionType().ultraWarm()) {
								level.playSound(null, base, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
								return 100;
							}
							level.setBlockAndUpdate(pos, this.blockToPlace.defaultBlockState());
						}
					}
				}
			}
		} else {
			BlockPos pos = base.above();
			if (this.blockToPlace instanceof LiquidBlock liquid && liquid.getFluid() == Fluids.WATER && player.getLevel().dimensionType().ultraWarm()) {
				level.playSound(null, base, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
				return 100;
			}
			if (level.getBlockState(pos).isAir() && this.blockToPlace.canSurvive(this.blockToPlace.defaultBlockState(), level, pos)) {
				level.setBlockAndUpdate(pos, this.blockToPlace.defaultBlockState());
			}
		}

		return 100;
	}
}
