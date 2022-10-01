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
	private final Map<ResourceLocation, Trophy> validTrophies = new HashMap<>();
	private final Map<ResourceLocation, Trophy> skippedTrophies = new HashMap<>();

	public TrophyReloadListener() {
		super(GSON, "trophies");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		this.validTrophies.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			try {
				JsonObject object = GsonHelper.convertToJsonObject(jsonElement, "trophy");
				Trophy trophy = Trophy.fromJson(object);
				//only add entries if the entity exists, otherwise skip it
				if (ForgeRegistries.ENTITY_TYPES.containsValue(trophy.type())) {
					this.validTrophies.put(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type()), trophy);
				} else {
					this.skippedTrophies.put(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type()), trophy);
				}
			} catch (Exception exception) {
				OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}! {}", resourceLocation, exception.getMessage());
			}
		});
		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs, and skipped {} configs.", this.validTrophies.size(), this.skippedTrophies.size());
		//we dont need this to stick around
		this.skippedTrophies.clear();
	}

	public Map<ResourceLocation, Trophy> getValidTrophies() {
		return this.validTrophies;
	}

	public static JsonElement serialize(Trophy trophy) {
		return GSON.toJsonTree(trophy);
	}

}
