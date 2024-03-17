package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.*;

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
		Map<ResourceLocation, Trophy> replacementMap = new HashMap<>();
		map.forEach((resourceLocation, jsonElement) -> {
			//check if the mod is loaded first. Since we read trophies before knowing the entity, any behaviors that are modded or use modded items will spit out an error.
			if (ModList.get().isLoaded(resourceLocation.getNamespace())) {
				try {
					Optional<WithConditions<Trophy>> conditionedTrophy = Trophy.CODEC.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow();
					if (conditionedTrophy.isPresent()) {
						Trophy trophy = conditionedTrophy.get().carrier();
						ResourceLocation mob = BuiltInRegistries.ENTITY_TYPE.getKey(trophy.type());
						if (trophy.replace()) {
							replacementMap.put(mob, trophy);
						} else {
							if (validTrophies.containsKey(mob)) {
								Trophy existing = validTrophies.get(mob);
								//create a new trophy with the combined variants. Since we now use a record for the trophy, the variant list is final and cant be modified using `add`.
								Trophy combinedTrophy = new Trophy.Builder(existing.type()).copyFrom(existing).addVariants(trophy.variants().right().orElse(new ArrayList<>())).build();
								validTrophies.put(BuiltInRegistries.ENTITY_TYPE.getKey(combinedTrophy.type()), combinedTrophy);
							} else if (BuiltInRegistries.ENTITY_TYPE.containsValue(trophy.type())) {
								validTrophies.put(mob, trophy);
							}
						}
					} else {
						OpenBlocksTrophies.LOGGER.debug("Skipped loading trophy {} as its conditions were not met", resourceLocation);
					}
				} catch (Exception exception) {
					OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}!", resourceLocation, exception);
				}
			}
		});

		//load all trophies marked to replace last, so they can properly replace other configs.
		//reload listeners don't respect datapack load order so this is the easiest way for me to handle this.
		validTrophies.putAll(replacementMap);

		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}

}
