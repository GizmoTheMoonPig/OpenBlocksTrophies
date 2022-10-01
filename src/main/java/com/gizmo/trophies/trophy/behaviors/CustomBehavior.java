package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class CustomBehavior {

	public abstract ResourceLocation getType();

	public final JsonObject serializeBehavior(JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.add("type", context.serialize(this.getType().toString()));
		this.serializeToJson(object, context);
		return object;
	}

	public abstract void serializeToJson(JsonObject object, JsonSerializationContext context);

	public abstract CustomBehavior fromJson(JsonObject object);

	public abstract int execute(TrophyBlockEntity block, Player player);
}
