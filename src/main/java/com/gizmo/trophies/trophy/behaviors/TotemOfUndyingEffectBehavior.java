package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TotemOfUndyingEffectBehavior extends CustomBehavior {

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("totem_of_undying");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		return new TotemOfUndyingEffectBehavior();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));
		return 100;
	}
}
