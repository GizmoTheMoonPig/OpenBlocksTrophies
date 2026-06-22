package com.gizmo.trophies.criteria;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public interface SpecialTrophyCriteria {

	TrophyCriteriaType getType();

	EntityType<?> entity();

	Optional<CompoundTag> variant();
}
