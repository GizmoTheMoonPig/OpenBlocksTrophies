package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public abstract class TrophyProvider implements DataProvider {

	protected final Map<ResourceLocation, Trophy> builder = Maps.newLinkedHashMap();
	private final DataGenerator generator;
	private final String modid;
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	public TrophyProvider(DataGenerator generator, String modid) {
		this.modid = modid;
		this.generator = generator;
	}

	@Override
	public void run(HashCache output) {
		Map<ResourceLocation, Trophy> map = Maps.newHashMap();
		this.builder.clear();
		this.createTrophies();
		map.putAll(this.builder);

		map.forEach((resourceLocation, trophy) -> {
			Path path = this.generator.getOutputFolder().resolve("data/" + this.modid + "/trophies/" + resourceLocation.getPath() + ".json");

			try {
				String s = GSON.toJson(TrophyReloadListener.serialize(trophy));
				String s1 = SHA1.hashUnencodedChars(s).toString();
				if (!Objects.equals(output.getHash(path), s1) || !Files.exists(path)) {
					Files.createDirectories(path.getParent());
					BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

					try {
						bufferedwriter.write(s);
					} catch (Throwable throwable1) {
						if (bufferedwriter != null) {
							try {
								bufferedwriter.close();
							} catch (Throwable throwable) {
								throwable1.addSuppressed(throwable);
							}
						}

						throw throwable1;
					}

					if (bufferedwriter != null) {
						bufferedwriter.close();
					}
				}

				output.putNew(path, s1);
			} catch (IOException ioexception) {
				OpenBlocksTrophies.LOGGER.error("Couldn't save trophy entry {}", path, ioexception);
			}
		});
	}

	protected abstract void createTrophies();

	/**
	 * Datagen a trophy here!
	 *
	 * @param trophy the trophy you want to make. A trophy takes the entity type at the very minimum. You can also specify the scale, vertical offset, drop chance, and custom right click behavior.
	 */
	protected void makeTrophy(Trophy trophy) {
		this.builder.putIfAbsent(new ResourceLocation(this.modid, Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(trophy.type())).getPath()), trophy);
	}

	@Override
	public String getName() {
		return this.modid + " Trophies";
	}
}
