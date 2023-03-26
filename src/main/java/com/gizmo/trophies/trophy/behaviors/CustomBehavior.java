package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

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

	//the number provided in the return value is the cooldown the trophy gets after clicking it.
	public abstract int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem);
}
