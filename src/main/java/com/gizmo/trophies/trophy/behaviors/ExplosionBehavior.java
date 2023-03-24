package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Explosion;

public class ExplosionBehavior extends CustomBehavior {

	private final float power;
	private final boolean destructive;

	public ExplosionBehavior() {
		this(0.0F, false);
	}

	public ExplosionBehavior(float power, boolean destructive) {
		this.power = power;
		this.destructive = destructive;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("explosion");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("power", context.serialize(this.power));
		object.add("destructive", context.serialize(this.destructive));
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		float power = GsonHelper.getAsFloat(object, "power");
		boolean destructive = GsonHelper.getAsBoolean(object, "destructive");
		return new ExplosionBehavior(power, destructive);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		BlockPos pos = block.getBlockPos();
		block.getLevel().explode(player, pos.getX(), pos.getY(), pos.getZ(), this.power, this.destructive ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE);
		return 0;
	}
}
