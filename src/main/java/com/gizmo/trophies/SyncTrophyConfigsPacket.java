package com.gizmo.trophies;

import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class SyncTrophyConfigsPacket {

	private final Map<ResourceLocation, Trophy> map;

	public SyncTrophyConfigsPacket(Map<ResourceLocation, Trophy> map) {
		this.map = map;
	}

	public SyncTrophyConfigsPacket(FriendlyByteBuf buf) {
		this.map = buf.readMap(FriendlyByteBuf::readResourceLocation, buf1 -> buf1.readJsonWithCodec(Trophy.CODEC));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeMap(this.map, FriendlyByteBuf::writeResourceLocation, (buf1, trophy) -> buf1.writeJsonWithCodec(Trophy.CODEC, trophy));
	}

	public static class Handler {
		@SuppressWarnings("Convert2Lambda")
		public static void handle(SyncTrophyConfigsPacket message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(new Runnable() {
				@Override
				public void run() {
					TrophyReloadListener.getValidTrophies().putAll(message.map);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
