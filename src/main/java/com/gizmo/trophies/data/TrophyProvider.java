package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;

public abstract class TrophyProvider implements DataProvider {

	private static final ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.put("neoforge:conditions", -1);
		map.put("type", 0);
		map.put("replace", 1);
		map.put("entity", 2);
		map.defaultReturnValue(3);
	});
	private static final Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing(s -> s);

	protected final Map<ResourceLocation, Trophy.Builder> builder = Maps.newLinkedHashMap();
	private final String modid;
	private final PackOutput.PathProvider entryPath;

	public TrophyProvider(PackOutput output, String modid) {
		this.modid = modid;
		this.entryPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "trophies");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		Map<ResourceLocation, Trophy.Builder> map = Maps.newHashMap();
		this.builder.clear();
		this.createTrophies();
		map.putAll(this.builder);

		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		for (Map.Entry<ResourceLocation, Trophy.Builder> entry : map.entrySet()) {
			Path path = this.entryPath.json(entry.getKey());
			futuresBuilder.add(this.saveTrophy(output, Trophy.CODEC.encodeStart(JsonOps.INSTANCE, Optional.of(new WithConditions<>(entry.getValue().conditions, entry.getValue().build()))).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow(), path));
		}
		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}

	protected abstract void createTrophies();

	/**
	 * Datagen a trophy here!
	 *
	 * @param trophy the trophy you want to make. A trophy takes the entity type at the very minimum. You can also specify the scale, vertical offset, drop chance, custom right click behavior, and NBT variants the trophy may have.
	 */
	protected void makeTrophy(Trophy.Builder trophy) {
		this.builder.putIfAbsent(new ResourceLocation(this.modid, Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophy.build().type())).getPath()), trophy);
	}

	@Override
	public String getName() {
		return this.modid + " Trophies";
	}

	//copy of DataProvider.saveStable but use our own key ordering instead
	private CompletableFuture<?> saveTrophy(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
		return CompletableFuture.runAsync(() -> {
			try {
				ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
				HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

				try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
					jsonwriter.setSerializeNulls(false);
					jsonwriter.setIndent("  ");
					GsonHelper.writeValue(jsonwriter, p_254542_, KEY_COMPARATOR);
				}

				p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
			} catch (IOException ioexception) {
				LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
			}
		}, Util.backgroundExecutor());
	}
}
