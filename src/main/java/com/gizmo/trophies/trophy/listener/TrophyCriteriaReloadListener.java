package com.gizmo.trophies.trophy.listener;

import com.gizmo.trophies.criteria.SpecialTrophyCriteria;
import com.gizmo.trophies.criteria.TrophyCriteriaType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrophyCriteriaReloadListener extends SimpleModCheckReloadListener<SpecialTrophyCriteria> {

	private static final Map<TrophyCriteriaType, List<SpecialTrophyCriteria>> criterias = new HashMap<>();

	public TrophyCriteriaReloadListener(HolderLookup.Provider registries) {
		super(registries, TrophyCriteriaType.DIRECT_CODEC, FileToIdConverter.json("trophies/criteria"));
	}

	public static Map<TrophyCriteriaType, List<SpecialTrophyCriteria>> getCriteriaMapper() {
		return criterias;
	}

	@Override
	protected void apply(Map<Identifier, SpecialTrophyCriteria> map, ResourceManager manager, ProfilerFiller filler) {
		criterias.clear();
		map.forEach((identifier, criteria) -> criterias.computeIfAbsent(criteria.getType(), ignored -> new ArrayList<>()).add(criteria));
	}
}
