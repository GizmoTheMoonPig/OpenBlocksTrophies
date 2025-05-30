package com.gizmo.trophies.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TrophyAdvancementProvider extends AdvancementProvider {

	public TrophyAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, List.of(new TrophyAdvancementGenerator()));
	}
}
