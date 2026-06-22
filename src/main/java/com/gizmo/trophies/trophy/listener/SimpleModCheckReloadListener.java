package com.gizmo.trophies.trophy.listener;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
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
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleModCheckReloadListener<T> extends SimplePreparableReloadListener<Map<Identifier, T>> {

	private final DynamicOps<JsonElement> ops;
	private final Codec<T> codec;
	private final FileToIdConverter converter;

	public SimpleModCheckReloadListener(HolderLookup.Provider registries, Codec<T> codec, FileToIdConverter converter) {
		this.ops = registries.createSerializationContext(JsonOps.INSTANCE);
		this.codec = codec;
		this.converter = converter;
	}

	//[VanillaCopy] of SimpleJsonResourceReloadListener.prepare, using our scanDirectory method
	@Override
	protected Map<Identifier, T> prepare(ResourceManager manager, ProfilerFiller profiler) {
		Map<Identifier, T> result = new HashMap<>();
		scanDirectory(manager, this.converter, this.makeConditionalOps(this.ops), this.codec, result);
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
							OpenBlocksTrophies.LOGGER.trace("Skipping loading file '{}' from '{}' as its conditions were not met", id, location);
						} else if (result.putIfAbsent(id, parsed.get()) != null) {
							throw new IllegalStateException("Duplicate file ignored with ID " + id);
						}
					}).ifError(error -> OpenBlocksTrophies.LOGGER.error("Couldn't parse file '{}' from '{}': {}", id, location, error));
				} catch (JsonParseException | IllegalArgumentException | IOException e) {
					OpenBlocksTrophies.LOGGER.error("Couldn't parse file '{}' from '{}'", id, location, e);
				}
			}
		}
	}
}
