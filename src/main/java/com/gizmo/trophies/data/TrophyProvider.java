package com.gizmo.trophies.data;

import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class TrophyProvider implements DataProvider {

	protected final Map<ResourceLocation, Trophy> builder = Maps.newLinkedHashMap();
	private final String modid;
	private final PackOutput.PathProvider entryPath;

	public TrophyProvider(PackOutput output, String modid) {
		this.modid = modid;
		this.entryPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "trophies");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		Map<ResourceLocation, Trophy> map = Maps.newHashMap();
		this.builder.clear();
		this.createTrophies();
		map.putAll(this.builder);

		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		map.forEach((resourceLocation, trophy) -> {
			Path path = this.entryPath.json(resourceLocation);
			futuresBuilder.add(DataProvider.saveStable(output, TrophyReloadListener.serialize(trophy), path));
		});
		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}

	protected abstract void createTrophies();

	/**
	 * Datagen a trophy here!
	 *
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
