package com.gizmo.trophies;

import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Map;

public record SyncTrophyConfigsPacket(Map<ResourceLocation, Trophy> trophies) implements CustomPacketPayload {

	public static final ResourceLocation ID = OpenBlocksTrophies.location("sync_trophy_configs");

	public SyncTrophyConfigsPacket(FriendlyByteBuf buf) {
		this(buf.readMap(FriendlyByteBuf::readResourceLocation, buf1 -> buf1.readJsonWithCodec(Trophy.BASE_CODEC)));
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeMap(this.trophies(), FriendlyByteBuf::writeResourceLocation, (buf1, trophy) -> buf1.writeJsonWithCodec(Trophy.BASE_CODEC, trophy));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(SyncTrophyConfigsPacket message, PlayPayloadContext ctx) {
			ctx.workHandler().execute(() -> {
				TrophyReloadListener.getValidTrophies().putAll(message.trophies());
				OpenBlocksTrophies.LOGGER.debug("Received {} trophy configs from server.", message.trophies().size());
			});
	}
}
