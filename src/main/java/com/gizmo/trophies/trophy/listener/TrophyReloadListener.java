package com.gizmo.trophies.trophy.listener;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrophyReloadListener extends SimpleModCheckReloadListener<Trophy> {

	private static final Map<Identifier, Trophy> validTrophies = new HashMap<>();

	public TrophyReloadListener(HolderLookup.Provider registries) {
		super(registries, Trophy.CODEC, FileToIdConverter.json("trophies"));
	}

	public static Map<Identifier, Trophy> getValidTrophies() {
		return validTrophies;
	}

	@Override
	protected void apply(Map<Identifier, Trophy> map, ResourceManager manager, ProfilerFiller profiler) {
		validTrophies.clear();
		Map<Identifier, Trophy> replacementMap = new HashMap<>();
		map.forEach((identifier, trophy) -> {
			if (!identifier.getPath().contains("criteria/")) {
				try {
					Identifier mob = BuiltInRegistries.ENTITY_TYPE.getKey(trophy.type());
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
				} catch (Exception exception) {
					OpenBlocksTrophies.LOGGER.error("Caught an error loading trophy config for {}!", identifier, exception);
				}
			}
		});

		//load all trophies marked to replace last, so they can properly replace other configs.
		//reload listeners don't respect datapack load order so this is the easiest way for me to handle this.
		validTrophies.putAll(replacementMap);

		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}
}
