package com.gizmo.trophies.criteria;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public abstract class AbstractTrophyCriteria implements SpecialTrophyCriteria {

	protected final EntityType<?> entity;
	protected final Optional<CompoundTag> variant;

	protected AbstractTrophyCriteria(EntityType<?> entity, Optional<CompoundTag> variant) {
		this.entity = entity;
		this.variant = variant;
	}

	@Override
	public final EntityType<?> entity() {
		return this.entity;
	}

	@Override
	public final Optional<CompoundTag> variant() {
		return this.variant;
	}

	protected static <T extends AbstractTrophyCriteria> Products.P2<RecordCodecBuilder.Mu<T>, EntityType<?>, Optional<CompoundTag>> baseCodec(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(o -> o.entity),
			CompoundTag.CODEC.optionalFieldOf("variant").forGetter(o -> o.variant));
	}
}
