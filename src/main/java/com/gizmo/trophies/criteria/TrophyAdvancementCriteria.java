package com.gizmo.trophies.criteria;

import com.gizmo.trophies.init.TrophyCriteria;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class TrophyAdvancementCriteria extends AbstractTrophyCriteria {

	public static final MapCodec<TrophyAdvancementCriteria> CODEC = RecordCodecBuilder.mapCodec(instance -> baseCodec(instance)
		.and(Identifier.CODEC.fieldOf("advancement").forGetter(o -> o.advancement))
		.apply(instance, TrophyAdvancementCriteria::new));

	private final Identifier advancement;

	public TrophyAdvancementCriteria(EntityType<?> entity, Optional<CompoundTag> variant, Identifier advancement) {
		super(entity, variant);
		this.advancement = advancement;
	}

	public Identifier getAdvancement() {
		return this.advancement;
	}

	@Override
	public TrophyCriteriaType getType() {
		return TrophyCriteria.ADVANCEMENT.get();
	}
}
