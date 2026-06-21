package com.gizmo.trophies.trophy;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.init.TrophyComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record TrophyInfoPredicate(EntityType<?> type, Optional<CompoundTag> variant) implements SingleComponentItemPredicate<TrophyInfo> {

	public static final Codec<TrophyInfoPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(TrophyInfoPredicate::type),
			CompoundTag.CODEC.optionalFieldOf("variant").forGetter(TrophyInfoPredicate::variant))
		.apply(instance, TrophyInfoPredicate::new));

	private TrophyInfoPredicate(EntityType<?> type) {
		this(type, Optional.empty());
	}

	private TrophyInfoPredicate(EntityType<?> type, CompoundTag variant) {
		this(type, Optional.of(variant));
	}

	public static DataComponentMatchers trophy(EntityType<?> type) {
		return DataComponentMatchers.Builder.components().partial(TrophyComponents.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(type)).build();
	}

	public static DataComponentMatchers variantTrophy(EntityType<?> type, CompoundTag variant) {
		return DataComponentMatchers.Builder.components().partial(TrophyComponents.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(type, variant)).build();
	}

	@Override
	public DataComponentType<TrophyInfo> componentType() {
		return TrophyComponents.TROPHY_INFO.get();
	}

	@Override
	public boolean matches(TrophyInfo value) {
		if (this.variant().isPresent() && !this.variant().get().equals(value.variant().orElse(new CompoundTag()))) {
			return false;
		}

		return this.type() == value.type();
	}
}
