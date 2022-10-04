package com.gizmo.trophies;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TrophyNetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(OpenBlocksTrophies.MODID, "channel"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	@SuppressWarnings("UnusedAssignment")
	public static void init() {
		int id = 0;
		CHANNEL.registerMessage(id++, SyncTrophyConfigsPacket.class, SyncTrophyConfigsPacket::encode, SyncTrophyConfigsPacket::new, SyncTrophyConfigsPacket.Handler::handle);
	}
}
