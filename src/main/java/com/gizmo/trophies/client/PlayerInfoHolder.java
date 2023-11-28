package com.gizmo.trophies.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record PlayerInfoHolder(RenderType type, @Nullable ResourceLocation cape, boolean slim) {

	private static final Map<String, GameProfile> GAMEPROFILE_CACHE = new HashMap<>();

	public static final List<String> TF_DEVS = List.of("benimatic", "drullkus", "tamaized", "jodlodi", "alphaleaf", "killer_demon", "gizmothemoonpig");
	public static final List<String> MOJANGSTAS = new ArrayList<>();

	public static PlayerInfoHolder getSkinFromName(String name) {
		RenderType type = RenderType.entityCutoutNoCullZOffset(DefaultPlayerSkin.getDefaultSkin());
		ResourceLocation cape = null;
		boolean slim = true;
		//MC names can't be shorter than 2 characters and shouldn't have any spaces either
		try {
			if (name.length() > 2 && !name.contains(" ")) {
				GameProfile profile = null;
				//fetch profile from cache if it exists already
				if (GAMEPROFILE_CACHE.containsKey(name))
					profile = GAMEPROFILE_CACHE.get(name);

				//if no cache exists build a new profile and fill it out
				//im using SkullBlockEntity.updateGameprofile to prevent a bunch of copy and paste. The method fills out the game profile with missing info.
				//using this also means I don't have to make my own executor, GameProfileCache, or Session Service instance
				if (profile == null) {
					GameProfile gameprofile1 = new GameProfile(null, name);
					if (!StringUtils.isBlank(gameprofile1.getName())) {
						SkullBlockEntity.updateGameprofile(gameprofile1, newProfile -> {
							if (newProfile != null) {
								GAMEPROFILE_CACHE.put(newProfile.getName().toLowerCase(), newProfile);
							}
						});
					}
				}
				if (profile != null) {
					Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().getInsecureSkinInformation(profile);

					if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
						MinecraftProfileTexture profileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
						type = RenderType.entityTranslucent(Minecraft.getInstance().getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
						slim = Objects.equals(profileTexture.getMetadata("model"), "slim");
					} else {
						UUID uuid = UUIDUtil.getOrCreatePlayerUUID(profile);
						//I think I prefer invalid skins being the steve texture. I'll keep this here in case I want to randomize it again one day
						//type = RenderType.entityCutoutNoCullZOffset(DefaultPlayerSkin.getDefaultSkin(uuid));
						slim = DefaultPlayerSkin.getSkinModelName(uuid).equals("slim");
					}
					if (map.containsKey(MinecraftProfileTexture.Type.CAPE)) {
						cape = Minecraft.getInstance().getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE);
						//do they have a mojang cape? Must be a mojang dev
						//there's 3 fucking mojang capes, WHY
						if ((cape.getPath().equals("capes/ea963f1b7d7c510da28800a770882d0c4b0aee6d") || cape.getPath().equals("capes/b5fa1ffb0f5b47d0803ca98b3759f12d5910760e") || cape.getPath().equals("capes/71716f0d5ebbc0b24f957d38cfc593906a3cdadf")) && !MOJANGSTAS.contains(name)) {
							MOJANGSTAS.add(name);
						}
					}
				}
			}
		} catch (Exception ignored) {

		}
		return new PlayerInfoHolder(type, cape, slim);
	}
}
