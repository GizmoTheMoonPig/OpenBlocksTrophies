package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.criteria.TrophyAdvancementCriteria;
import com.gizmo.trophies.criteria.TrophyCriteriaType;
import com.gizmo.trophies.criteria.TrophyInteractionCriteria;
import com.gizmo.trophies.criteria.TrophySpecialKillCriteria;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TrophyCriteria {

	public static final DeferredRegister<TrophyCriteriaType> TROPHY_CRITERIA = DeferredRegister.create(OpenBlocksTrophies.TROPHY_CRITERIA_KEY, OpenBlocksTrophies.MODID);

	public static final DeferredHolder<TrophyCriteriaType, TrophyCriteriaType> ADVANCEMENT = TROPHY_CRITERIA.register("advancement", () -> new TrophyCriteriaType(TrophyAdvancementCriteria.CODEC));
	public static final DeferredHolder<TrophyCriteriaType, TrophyCriteriaType> INTERACTION = TROPHY_CRITERIA.register("entity_interaction", () -> new TrophyCriteriaType(TrophyInteractionCriteria.CODEC));
	public static final DeferredHolder<TrophyCriteriaType, TrophyCriteriaType> SPECIAL_KILL = TROPHY_CRITERIA.register("special_kill", () -> new TrophyCriteriaType(TrophySpecialKillCriteria.CODEC));
}
