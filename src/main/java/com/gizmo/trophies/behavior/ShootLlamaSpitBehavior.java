package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ShootLlamaSpitBehavior() implements CustomBehavior {

	public static final Codec<ShootLlamaSpitBehavior> CODEC = Codec.unit(ShootLlamaSpitBehavior::new);

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.LLAMA_SPIT.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos pos = block.getBlockPos();
		Level level = player.level();

		LlamaSpit spit = new LlamaSpit(EntityType.LLAMA_SPIT, level);
		spit.setPos(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5D);

		double dX = player.getX() - (pos.getX() + 0.5F);
		double dy = player.getBoundingBox().minY + player.getEyeY() / 3.0F - spit.getY();
		double dZ = player.getZ() - (pos.getZ() + 0.5F);
		float f = Mth.sqrt((float) (dX * dX + dZ * dZ)) * 0.2F;
		spit.shoot(dX, dy + f, dZ, 1.5F, 10.0F);
		level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LLAMA_SPIT, SoundSource.NEUTRAL, 1.0F, 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F);
		level.addFreshEntity(spit);

		return 10;
	}
}
