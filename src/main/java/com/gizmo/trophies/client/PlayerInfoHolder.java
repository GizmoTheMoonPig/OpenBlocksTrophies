package com.gizmo.trophies.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record PlayerInfoHolder(RenderType type, @Nullable Identifier cape, boolean slim, boolean upsideDown) {

	public static final List<String> TF_DEVS = List.of("benimatic", "drullkus", "tamaized", "jodlodi", "alphaleaf", "killer_demon", "gizmothemoonpig", "sleepy_horse");
	public static final List<String> MOJANGSTAS = new ArrayList<>(List.of("notch"));

	public static PlayerInfoHolder getSkinFromProfile(@Nullable ResolvableProfile profile) {
		RenderType type = PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE;
		Identifier cape = null;
		boolean slim = true;
		if (profile == null) return new PlayerInfoHolder(type, null, true, false);
		String name = profile.name().orElse("").toLowerCase(Locale.ROOT);
		boolean upsideDown = name.equalsIgnoreCase("dinnerbone") || name.equalsIgnoreCase("grumm");
		try {
			//MC names can't be shorter than 2 characters and shouldn't have any spaces either
			if (name.length() > 2 && !name.contains(" ") && StringUtil.isValidPlayerName(name)) {
				PlayerSkinRenderCache.RenderInfo skinProfile = Minecraft.getInstance().playerSkinRenderCache().getOrDefault(profile);
				PlayerSkin skin = skinProfile.playerSkin();
				type = skinProfile.renderType();
				slim = skin.model() == PlayerModelType.SLIM;
				//do they have a mojang cape? Must be a mojang dev
				//there's 3 fucking mojang capes, WHY
				if (skin.cape() != null) {
					cape = skin.cape().texturePath();
					if ((cape.getPath().equals("capes/ea963f1b7d7c510da28800a770882d0c4b0aee6d") ||
						cape.getPath().equals("capes/b5fa1ffb0f5b47d0803ca98b3759f12d5910760e") ||
						cape.getPath().equals("capes/71716f0d5ebbc0b24f957d38cfc593906a3cdadf")) && !MOJANGSTAS.contains(name)) {
						MOJANGSTAS.add(name);
					}
				}
			}
		} catch (Exception ignored) {

		}
		return new PlayerInfoHolder(type, cape, slim, upsideDown);
	}
}
