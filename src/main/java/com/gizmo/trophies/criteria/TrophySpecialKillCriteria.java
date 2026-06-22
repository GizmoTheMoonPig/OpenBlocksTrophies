package com.gizmo.trophies.criteria;

import com.gizmo.trophies.init.TrophyCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class TrophySpecialKillCriteria extends AbstractTrophyCriteria {

	public static final MapCodec<TrophySpecialKillCriteria> CODEC = RecordCodecBuilder.mapCodec(instance ->
		baseCodec(instance).and(instance.group(
				DamageSourcePredicate.CODEC.fieldOf("source").forGetter(o -> o.source),
				Codec.floatRange(0.0F, 1.0F).fieldOf("drop_chance").forGetter(o -> o.dropChance)))
			.apply(instance, TrophySpecialKillCriteria::new));

	private final DamageSourcePredicate source;
	private final float dropChance;

	public TrophySpecialKillCriteria(EntityType<?> entity, Optional<CompoundTag> variant, DamageSourcePredicate source, float dropChance) {
		super(entity, variant);
		this.source = source;
		this.dropChance = dropChance;
	}

	public DamageSourcePredicate getSource() {
		return this.source;
	}

	public float getDropChance() {
		return this.dropChance;
	}

	@Override
	public TrophyCriteriaType getType() {
		return TrophyCriteria.SPECIAL_KILL.get();
	}
}
