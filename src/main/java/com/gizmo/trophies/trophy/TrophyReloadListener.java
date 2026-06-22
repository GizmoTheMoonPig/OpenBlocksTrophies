package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrophyReloadListener extends SimplePreparableReloadListener<Map<Identifier, Trophy>> {

	private static final Map<Identifier, Trophy> validTrophies = new HashMap<>();

	private final DynamicOps<JsonElement> ops;

	public TrophyReloadListener(HolderLookup.Provider registries) {
		this.ops = registries.createSerializationContext(JsonOps.INSTANCE);
	}

	public static Map<Identifier, Trophy> getValidTrophies() {
		return validTrophies;
	}

	@Override
	protected void apply(Map<Identifier, Trophy> map, ResourceManager manager, ProfilerFiller profiler) {
		validTrophies.clear();
		Map<Identifier, Trophy> replacementMap = new HashMap<>();
		map.forEach((identifier, trophy) -> {
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
		});

		//load all trophies marked to replace last, so they can properly replace other configs.
		//reload listeners don't respect datapack load order so this is the easiest way for me to handle this.
		validTrophies.putAll(replacementMap);

		OpenBlocksTrophies.LOGGER.info("Loaded {} Trophy configs.", validTrophies.size());
	}

	//[VanillaCopy] of SimpleJsonResourceReloadListener.prepare, using our scanDirectory method
	@Override
	protected Map<Identifier, Trophy> prepare(ResourceManager manager, ProfilerFiller profiler) {
		Map<Identifier, Trophy> result = new HashMap<>();
		scanDirectory(manager, FileToIdConverter.json("trophies"), this.makeConditionalOps(this.ops), Trophy.CODEC, result);
		return result;
	}

	//[VanillaCopy] of SimpleJsonResourceReloadListener.scanDirectory, just with a check to see if the mod is actually loaded or not
	public static <T> void scanDirectory(ResourceManager manager, FileToIdConverter lister, DynamicOps<JsonElement> ops, Codec<T> codec, Map<Identifier, T> result) {
		var conditionalCodec = ConditionalOps.createConditionalCodec(codec);
		for (Map.Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
			Identifier location = entry.getKey();
			//Check if mod is loaded before doing anything first
			if (ModList.get().isLoaded(location.getNamespace())) {
				Identifier id = lister.fileToId(location);

				try (Reader reader = entry.getValue().openAsReader()) {
					conditionalCodec.parse(ops, JsonParser.parseReader(reader)).ifSuccess(parsed -> {
						if (parsed.isEmpty()) {
							//note: logging was lowered to TRACE here as I hate how much it spams the log otherwise
							OpenBlocksTrophies.LOGGER.trace("Skipping loading trophy '{}' from '{}' as its conditions were not met", id, location);
						} else if (result.putIfAbsent(id, parsed.get()) != null) {
							throw new IllegalStateException("Duplicate trophy ignored with ID " + id);
						}
					}).ifError(error -> OpenBlocksTrophies.LOGGER.error("Couldn't parse trophy '{}' from '{}': {}", id, location, error));
				} catch (JsonParseException | IllegalArgumentException | IOException e) {
					OpenBlocksTrophies.LOGGER.error("Couldn't parse trophy '{}' from '{}'", id, location, e);
				}
			}
		}
	}
}
