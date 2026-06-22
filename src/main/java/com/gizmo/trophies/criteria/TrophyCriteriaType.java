package com.gizmo.trophies.criteria;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record TrophyCriteriaType(MapCodec<? extends SpecialTrophyCriteria> codec) {

	public static final Codec<SpecialTrophyCriteria> DIRECT_CODEC = Codec.lazyInitialized(OpenBlocksTrophies.TROPHY_CRITERIA::byNameCodec).dispatch("type", SpecialTrophyCriteria::getType, TrophyCriteriaType::codec);
}
