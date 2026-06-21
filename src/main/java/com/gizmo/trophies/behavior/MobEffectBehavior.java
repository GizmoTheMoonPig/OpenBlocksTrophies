package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.init.TrophyBehaviors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

public record MobEffectBehavior(Holder<MobEffect> effect, int time, int amplifier) implements CustomBehavior {

	public static final MapCodec<MobEffectBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(MobEffectBehavior::effect),
			Codec.INT.fieldOf("time").forGetter(MobEffectBehavior::time),
			Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectBehavior::amplifier)
	).apply(instance, MobEffectBehavior::new));

	@Override
	public CustomBehaviorType getType() {
		return TrophyBehaviors.MOB_EFFECT.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.addEffect(new MobEffectInstance(this.effect(), this.time(), this.amplifier()));
		return this.time();
	}
}
