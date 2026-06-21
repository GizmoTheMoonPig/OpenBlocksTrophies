package com.gizmo.trophies.datagen;

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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
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

	protected final Map<Identifier, Trophy.Builder> builder = Maps.newLinkedHashMap();
	private final CompletableFuture<HolderLookup.Provider> registriesLookup;
	private final String modid;
	private final PackOutput.PathProvider entryPath;

	public TrophyProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
		this.registriesLookup = registries;
		this.modid = modid;
		this.entryPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "trophies");
	}

	@Override
	public final CompletableFuture<?> run(CachedOutput output) {
		return this.registriesLookup.thenCompose(provider -> this.run(output, provider));
	}

	public CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
		Map<Identifier, Trophy.Builder> map = Maps.newHashMap();
		this.builder.clear();
		this.createTrophies(provider);
		map.putAll(this.builder);

		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
		for (Map.Entry<Identifier, Trophy.Builder> entry : map.entrySet()) {
			Path path = this.entryPath.json(entry.getKey());
			futuresBuilder.add(this.saveTrophy(output, Trophy.CODEC.encodeStart(ops, Optional.of(new WithConditions<>(entry.getValue().loadConditions, entry.getValue().build()))).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow(), path));
		}
		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}

	protected abstract void createTrophies(HolderLookup.Provider provider);

	/**
	 * Datagen a trophy here!
	 *
	 * @param trophy the trophy you want to make. A trophy takes the entity type at the very minimum. You can also specify the scale, vertical offset, drop chance, custom right click behavior, and NBT variants the trophy may have.
	 */
	protected void makeTrophy(Trophy.Builder trophy) {
		this.builder.putIfAbsent(Identifier.fromNamespaceAndPath(this.modid, Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophy.build().type())).getPath()), trophy);
	}

	@Override
	public String getName() {
		return this.modid + " Trophies";
	}

	//copy of DataProvider.saveStable but use our own key ordering instead
	private CompletableFuture<?> saveTrophy(CachedOutput output, JsonElement element, Path path) {
		return CompletableFuture.runAsync(() -> {
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				HashingOutputStream hashedBytes = new HashingOutputStream(Hashing.sha256(), bytes);

				try (JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashedBytes, StandardCharsets.UTF_8))) {
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					GsonHelper.writeValue(jsonWriter, element, KEY_COMPARATOR);
				}

				output.writeIfNeeded(path, bytes.toByteArray(), hashedBytes.hash());
			} catch (IOException ioexception) {
				LOGGER.error("Failed to save file to {}", path, ioexception);
			}
		}, Util.backgroundExecutor().forName("saveTrophy"));
	}
}
