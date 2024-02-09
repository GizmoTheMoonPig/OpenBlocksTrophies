package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ShootArrowBehavior(int amount, Optional<MobEffect> effect) implements CustomBehavior {

	public static final Codec<ShootArrowBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("amount", 1).forGetter(ShootArrowBehavior::amount),
			BuiltInRegistries.MOB_EFFECT.byNameCodec().optionalFieldOf("effect").orElse(null).forGetter(ShootArrowBehavior::effect)
	).apply(instance, ShootArrowBehavior::new));

	public ShootArrowBehavior(int amount, @Nullable MobEffect effect) {
		this(amount, Optional.ofNullable(effect));
	}

	public ShootArrowBehavior() {
		this(1, Optional.empty());
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.ARROW.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos pos = block.getBlockPos();
		Level level = player.level();

		for (int i = 0; i < this.amount(); i++) {
			Arrow arrow = new Arrow(level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5D, new ItemStack(Items.ARROW));
			arrow.setBaseDamage(0.1D);
			arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			if (this.effect().isPresent()) arrow.addEffect(new MobEffectInstance(this.effect().get()));
			arrow.shoot(level.getRandom().nextInt(10) - 5, 40, level.getRandom().nextInt(10) - 5, 1.0F, 6.0F);
			level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
			level.addFreshEntity(arrow);
		}
		return 10 * this.amount();
	}
}
