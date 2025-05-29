package com.gizmo.trophies.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public record PlayerInfoHolder(RenderType type, @Nullable ResourceLocation cape, boolean slim) {

	private static final Map<String, GameProfile> GAMEPROFILE_CACHE = new HashMap<>();

	public static final List<String> TF_DEVS = List.of("benimatic", "drullkus", "tamaized", "jodlodi", "alphaleaf", "killer_demon", "gizmothemoonpig");
	public static final List<String> MOJANGSTAS = new ArrayList<>();

	public static PlayerInfoHolder getSkinFromName(String name) {
		RenderType type = RenderType.entityCutoutNoCullZOffset(DefaultPlayerSkin.getDefaultTexture());
		ResourceLocation cape = null;
		boolean slim = true;
		//MC names can't be shorter than 2 characters and shouldn't have any spaces either
		try {
			if (name.length() > 2 && !name.contains(" ") && StringUtil.isValidPlayerName(name)) {
				//fetch profile from cache if it exists already
				GameProfile profile = GAMEPROFILE_CACHE.get(name);

				//if no cache exists build a new profile and fill it out
				//im using SkullBlockEntity.fetchGameProfile to prevent a bunch of copy and paste. The method fills out the game profile with missing info.
				//using this also means I don't have to make my own executor, GameProfileCache, or Session Service instance
				if (profile == null) {
					SkullBlockEntity.fetchGameProfile(name).thenAccept(newProfile ->
						newProfile.ifPresent(gameProfile ->
							GAMEPROFILE_CACHE.put(name, gameProfile)));
				}

				//attempt to fetch the profile after filling it out
				profile = GAMEPROFILE_CACHE.get(name);

				if (profile != null) {
					SkinManager manager = Minecraft.getInstance().getSkinManager();
					Optional<PlayerSkin> skin = manager.getOrLoad(profile).getNow(null);
					if (skin.isPresent()) {
						type = RenderType.entityTranslucent(skin.get().texture());
						slim = skin.get().model().equals(PlayerSkin.Model.SLIM);
						//do they have a mojang cape? Must be a mojang dev
						//there's 3 fucking mojang capes, WHY
						if (skin.get().capeTexture() != null) {
							cape = skin.get().capeTexture();
							if ((cape.getPath().equals("capes/ea963f1b7d7c510da28800a770882d0c4b0aee6d") ||
								cape.getPath().equals("capes/b5fa1ffb0f5b47d0803ca98b3759f12d5910760e") ||
								cape.getPath().equals("capes/71716f0d5ebbc0b24f957d38cfc593906a3cdadf")) && !MOJANGSTAS.contains(name)) {
								MOJANGSTAS.add(name);
							}
						}
					}
				}
			}
		} catch (Exception ignored) {

		}
		return new PlayerInfoHolder(type, cape, slim);
	}
}
