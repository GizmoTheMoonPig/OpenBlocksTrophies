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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ShootArrowBehavior extends CustomBehavior {

	private final int amount;
	private final MobEffect arrowEffect;

	public ShootArrowBehavior() {
		this(1, null);
	}

	public ShootArrowBehavior(int amount, @Nullable MobEffect arrowEffect) {
		this.amount = amount;
		this.arrowEffect = arrowEffect;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("arrow");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("amount", context.serialize(this.amount));
		if (this.arrowEffect != null) {
			object.add("effect", context.serialize(ForgeRegistries.MOB_EFFECTS.getKey(this.arrowEffect).toString()));
		}
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		int amount = GsonHelper.getAsInt(object, "amount", 1);
		MobEffect effect = null;
		if (object.has("effect")) {
			effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(object, "effect")));
		}
		return new ShootArrowBehavior(amount, effect);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		BlockPos pos = block.getBlockPos();
		Level level = block.getLevel();

		for (int i = 0; i < this.amount; i++) {
			Arrow arrow = new Arrow(level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5D);
			arrow.setBaseDamage(0.1D);
			arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			if (this.arrowEffect != null) arrow.addEffect(new MobEffectInstance(this.arrowEffect));
			arrow.shoot(level.getRandom().nextInt(10) - 5, 40, level.getRandom().nextInt(10) - 5, 1.0F, 6.0F);
			level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
			level.addFreshEntity(arrow);
		}
		return 0;
	}
}
