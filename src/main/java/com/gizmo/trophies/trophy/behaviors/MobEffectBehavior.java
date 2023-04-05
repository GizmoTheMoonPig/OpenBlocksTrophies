package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class MobEffectBehavior extends CustomBehavior {

	private final MobEffect effect;
	private final int time;
	private final int amplifier;

	public MobEffectBehavior() {
		this(MobEffects.WITHER, 0, 0);
	}

	public MobEffectBehavior(MobEffect effect, int time, int amplifier) {
		this.effect = effect;
		this.time = time;
		this.amplifier = amplifier;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("mob_effect");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("effect", context.serialize(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(this.effect)).toString()));
		object.add("time", context.serialize(this.time));
		object.add("amplifier", context.serialize(this.amplifier));
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(object, "effect")));
		int time = GsonHelper.getAsInt(object, "time", 100);
		int amplifier = GsonHelper.getAsInt(object, "amplifier", 0);
		return new MobEffectBehavior(Objects.requireNonNull(effect), time, amplifier);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.addEffect(new MobEffectInstance(this.effect, this.time, this.amplifier));
		return this.time;
	}
}
