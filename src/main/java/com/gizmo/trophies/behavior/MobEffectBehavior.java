package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public record MobEffectBehavior(MobEffect effect, int time, int amplifier) implements CustomBehavior {

	public static final Codec<MobEffectBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectBehavior::effect),
			Codec.INT.fieldOf("time").forGetter(MobEffectBehavior::time),
			Codec.INT.fieldOf("amplifier").forGetter(MobEffectBehavior::amplifier)
	).apply(instance, MobEffectBehavior::new));

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.MOB_EFFECT.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.addEffect(new MobEffectInstance(this.effect(), this.time(), this.amplifier()));
		return this.time();
	}
}
