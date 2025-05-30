package com.gizmo.trophies.trophy;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
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

	public TrophyInfoPredicate(EntityType<?> type) {
		this(type, Optional.empty());
	}

	public TrophyInfoPredicate(EntityType<?> type, CompoundTag variant) {
		this(type, Optional.of(variant));
	}

	@Override
	public DataComponentType<TrophyInfo> componentType() {
		return TrophyRegistries.TROPHY_INFO.get();
	}

	@Override
	public boolean matches(TrophyInfo value) {
		if (this.variant().isPresent() && !this.variant().get().equals(value.variant().orElse(new CompoundTag()))) {
			return false;
		}

		return this.type() == value.type();
	}
}
