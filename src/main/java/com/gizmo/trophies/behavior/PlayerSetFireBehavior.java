package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.init.TrophyBehaviors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record PlayerSetFireBehavior(int time) implements CustomBehavior {

	public static final MapCodec<PlayerSetFireBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("seconds").forGetter(PlayerSetFireBehavior::time)
	).apply(instance, PlayerSetFireBehavior::new));

	@Override
	public CustomBehaviorType getType() {
		return TrophyBehaviors.SET_FIRE.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.igniteForSeconds(this.time());
		return 0;
	}
}
