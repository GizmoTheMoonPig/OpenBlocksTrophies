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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class TrophyReloadListener extends SimpleJsonResourceReloadListener {

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Trophy.class, new Trophy.Serializer()).create();
	private static final Map<ResourceLocation, Trophy> validTrophies = new HashMap<>();

	public TrophyReloadListener() {
		super(GSON, "trophies");
	}

	public static Map<ResourceLocation, Trophy> getValidTrophies() {
		return validTrophies;
	}

	public static JsonElement serialize(Trophy trophy) {
		return GSON.toJsonTree(trophy);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		validTrophies.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			//check if the mod is loaded first. Since we read trophies before knowing the entity, any behaviors that are modded or use modded items will spit out an error.
			if (ModList.get().isLoaded(resourceLocation.getNamespace())) {
				try {
					JsonObject object = GsonHelper.convertToJsonObject(jsonElement, "trophy");
					Trophy trophy = Trophy.fromJson(object);
					//only add entries if the entity exists, otherwise skip it
					if (ForgeRegistries.ENTITY_TYPES.containsValue(trophy.type())) {
						validTrophies.put(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type()), trophy);
					}
				} catch (Exception exception) {
					OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}! {}", resourceLocation, exception.getMessage());
				}
			}
		});
		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}

}
