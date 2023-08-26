package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TrophyReloadListener extends SimpleJsonResourceReloadListener {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final TreeMap<ResourceLocation, Trophy> validTrophies = new TreeMap<>();

	public TrophyReloadListener() {
		super(GSON, "trophies");
	}

	public static TreeMap<ResourceLocation, Trophy> getValidTrophies() {
		return validTrophies;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		validTrophies.clear();
		map.forEach((resourceLocation, jsonElement) -> {
			//check if the mod is loaded first. Since we read trophies before knowing the entity, any behaviors that are modded or use modded items will spit out an error.
			if (ModList.get().isLoaded(resourceLocation.getNamespace())) {
				try {
					Trophy trophy = Trophy.CODEC.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow();
					ResourceLocation mob = ForgeRegistries.ENTITY_TYPES.getKey(trophy.type());
					if (validTrophies.containsKey(mob)) {
						Trophy existing = validTrophies.get(mob);
						//create a new trophy with the combined variants. Since we now use a record for the trophy, the variant list is final and cant be modified using `add`.
						Trophy combinedTrophy = new Trophy.Builder(existing.type()).copyFrom(existing).addVariants(trophy.variants().right().orElse(new ArrayList<>())).build();
						validTrophies.put(ForgeRegistries.ENTITY_TYPES.getKey(combinedTrophy.type()), combinedTrophy);
					} else if (ForgeRegistries.ENTITY_TYPES.containsValue(trophy.type())) {
						validTrophies.put(mob, trophy);
					}
				} catch (Exception exception) {
					OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}! {}", resourceLocation, exception.getMessage());
				}
			}
		});
		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}

}
