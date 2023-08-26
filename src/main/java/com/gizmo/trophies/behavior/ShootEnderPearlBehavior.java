package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ShootEnderPearlBehavior() implements CustomBehavior {

	public static final Codec<ShootEnderPearlBehavior> CODEC = Codec.unit(ShootEnderPearlBehavior::new);

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.ENDER_PEARL.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		Level level = player.level();
		ThrownEnderpearl pearl = new ThrownEnderpearl(level, player);
		BlockPos pos = block.getBlockPos();
		pearl.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		pearl.setDeltaMovement(level.getRandom().nextGaussian(), 1.0D, level.getRandom().nextGaussian());
		level.addFreshEntity(pearl);
		return 10;
	}
}
