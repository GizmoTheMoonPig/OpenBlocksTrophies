package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class PlayerSetFireBehavior extends CustomBehavior {

	private final int time;

	public PlayerSetFireBehavior() {
		this(0);
	}

	public PlayerSetFireBehavior(int time) {
		this.time = time;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("set_fire");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("seconds", context.serialize(this.time));
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		return new PlayerSetFireBehavior(GsonHelper.getAsInt(object, "seconds"));
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		player.setSecondsOnFire(this.time);
		return 0;
	}
}
