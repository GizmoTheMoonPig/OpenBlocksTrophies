package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class TrophyReloadListener extends SimpleJsonResourceReloadListener {

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Trophy.class, new Trophy.Serializer()).create();
	private static final Map<ResourceLocation, Trophy> validTrophies = new HashMap<>();

	public TrophyReloadListener() {
		super(GSON, "trophies");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		validTrophies.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			try {
				JsonObject object = GsonHelper.convertToJsonObject(jsonElement, "trophy");
				Trophy trophy = Trophy.fromJson(object);
				//only add entries if the entity exists, otherwise skip it
				if (ForgeRegistries.ENTITIES.containsValue(trophy.type())) {
					validTrophies.put(ForgeRegistries.ENTITIES.getKey(trophy.type()), trophy);
				}
			} catch (Exception exception) {
				OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}! {}", resourceLocation, exception.getMessage());
			}
		});
		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}



	public static Map<ResourceLocation, Trophy> getValidTrophies() {
		return validTrophies;
	}

	public static JsonElement serialize(Trophy trophy) {
		return GSON.toJsonTree(trophy);
	}

}
