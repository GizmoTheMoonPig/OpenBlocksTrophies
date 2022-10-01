package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;

public class ShootEnderPearlBehavior extends CustomBehavior {

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("ender_pearl");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		return new ShootEnderPearlBehavior();
	}

	@Override
	public int execute(TrophyBlockEntity block, Player player) {
		Level level = block.getLevel();
		ThrownEnderpearl pearl = new ThrownEnderpearl(level, player);
		BlockPos pos = block.getBlockPos();
		pearl.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		pearl.setDeltaMovement(level.getRandom().nextGaussian(), 1.0D, level.getRandom().nextGaussian());
		level.addFreshEntity(pearl);
		return 10;
	}
}
