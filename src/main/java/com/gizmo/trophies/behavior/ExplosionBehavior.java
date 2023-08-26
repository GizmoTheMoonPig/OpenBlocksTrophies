package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

public record ExplosionBehavior(float power, boolean destructive) implements CustomBehavior {

	public static final Codec<ExplosionBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("power").forGetter(ExplosionBehavior::power),
			Codec.BOOL.fieldOf("destructive").forGetter(ExplosionBehavior::destructive)
	).apply(instance, ExplosionBehavior::new));

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.EXPLOSION.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos pos = block.getBlockPos();
		Objects.requireNonNull(block.getLevel()).explode(player, pos.getX(), pos.getY(), pos.getZ(), this.power(), this.destructive() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
		return 0;
	}
}
