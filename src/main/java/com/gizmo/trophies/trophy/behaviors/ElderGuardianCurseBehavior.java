package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ElderGuardianCurseBehavior extends CustomBehavior {

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("guardian_curse");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		return new ElderGuardianCurseBehavior();
	}

	@Override
	public int execute(TrophyBlockEntity block, Player player) {
		if (player instanceof ServerPlayer sp) {
			sp.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 0.0F));
		}
		return 100;
	}
}
