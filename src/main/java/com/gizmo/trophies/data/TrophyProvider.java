package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import com.google.common.collect.Maps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public abstract class TrophyProvider implements DataProvider {

	private final String modid;
	private final DataGenerator.PathProvider entryPath;
	protected final Map<ResourceLocation, Trophy> builder = Maps.newLinkedHashMap();

	public TrophyProvider(DataGenerator generator, String modid) {
		this.modid = modid;
		this.entryPath = generator.createPathProvider(DataGenerator.Target.DATA_PACK, "trophies");
	}

	@Override
	public void run(CachedOutput output) {
		Map<ResourceLocation, Trophy> map = Maps.newHashMap();
		this.builder.clear();
		this.createTrophies();
		map.putAll(this.builder);

		map.forEach((resourceLocation, trophy) -> {
			Path path = this.entryPath.json(resourceLocation);

			try {
				DataProvider.saveStable(output, TrophyReloadListener.serialize(trophy), path);
			} catch (IOException ioexception) {
				OpenBlocksTrophies.LOGGER.error("Couldn't save trophy entry {}", path, ioexception);
			}
		});
	}

	protected abstract void createTrophies();

	/**
	 * Datagen a trophy here!
	 * @param trophy the trophy you want to make. A trophy takes the entity type at the very minimum. You can also specify the scale, vertical offset, drop chance, and custom right click behavior.
	 */
	protected void makeTrophy(Trophy trophy) {
		this.builder.putIfAbsent(new ResourceLocation(this.modid, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type())).getPath()), trophy);
	}

	@Override
	public String getName() {
		return this.modid + " Trophies";
	}
}
