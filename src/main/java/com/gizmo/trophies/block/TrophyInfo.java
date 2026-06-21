package com.gizmo.trophies.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record TrophyInfo(EntityType<?> type, Optional<CompoundTag> variant, Optional<Unit> cycling, Optional<Integer> cooldown, boolean baby) {

	public static final TrophyInfo DEFAULT = new TrophyInfo(EntityType.CHICKEN, Optional.empty(), Optional.empty(), Optional.empty(), false);

	public static final Codec<TrophyInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(TrophyInfo::type),
			CompoundTag.CODEC.optionalFieldOf("variant").forGetter(TrophyInfo::variant),
			Unit.CODEC.optionalFieldOf("cycling").forGetter(TrophyInfo::cycling),
			Codec.INT.optionalFieldOf("cooldown").forGetter(TrophyInfo::cooldown),
			Codec.BOOL.optionalFieldOf("baby", false).forGetter(TrophyInfo::baby))
		.apply(instance, TrophyInfo::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TrophyInfo> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.ENTITY_TYPE), TrophyInfo::type,
		ByteBufCodecs.optional(ByteBufCodecs.fromCodec(CompoundTag.CODEC)), TrophyInfo::variant,
		ByteBufCodecs.optional(StreamCodec.unit(Unit.INSTANCE)), TrophyInfo::cycling,
		ByteBufCodecs.optional(ByteBufCodecs.INT), TrophyInfo::cooldown,
		ByteBufCodecs.BOOL, TrophyInfo::baby,
		TrophyInfo::new);

	public TrophyInfo(EntityType<?> type) {
		this(type, Optional.empty(), Optional.empty(), Optional.empty(), false);
	}

	public TrophyInfo(EntityType<?> type, CompoundTag variant) {
		this(type, !variant.isEmpty() ? Optional.of(variant) : Optional.empty(), Optional.empty(), Optional.empty(), false);
	}

	public TrophyInfo(EntityType<?> type, boolean cycling) {
		this(type, Optional.empty(), cycling ? Optional.of(Unit.INSTANCE) : Optional.empty(), Optional.empty(), false);
	}

	public TrophyInfo withVariant(@Nullable CompoundTag variant) {
		return new TrophyInfo(this.type(), Optional.ofNullable(variant), this.cycling(), this.cooldown(), this.baby());
	}

	public TrophyInfo withCooldown(int cooldown) {
		return new TrophyInfo(this.type(), this.variant(), this.cycling(), Optional.of(cooldown), this.baby());
	}

	public TrophyInfo setBaby(boolean baby) {
		return new TrophyInfo(this.type(), this.variant(), this.cycling(), this.cooldown(), baby);
	}

}
