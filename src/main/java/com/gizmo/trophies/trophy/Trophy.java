package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.behavior.CustomBehavior;
import com.gizmo.trophies.behavior.CustomBehaviorType;
import com.gizmo.trophies.trophy.listener.TrophyReloadListener;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO drop chance array (for variants)
//if array is 1 entry that value is used for all variants
//otherwise check that the array is the same size as the variant list
public record Trophy(boolean replace, EntityType<?> type, double dropChance, Vector3fc offset, Vector3fc rotation, float scale, Optional<CustomBehavior> clickBehavior, Either<Pair<String, Identifier>, List<CompoundTag>> variants, Optional<CompoundTag> defaultData, Optional<SoundEvent> clickSoundOverride) {

	public static final double DEFAULT_DROP_CHANCE = 0.001D;
	public static final double BOSS_DROP_CHANCE = 0.0075D;

	public static final Codec<Trophy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("replace", false).forGetter(Trophy::replace),
		BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(Trophy::type),
		Codec.DOUBLE.optionalFieldOf("drop_chance", DEFAULT_DROP_CHANCE).forGetter(Trophy::dropChance),
		ExtraCodecs.VECTOR3F.optionalFieldOf("offset", new Vector3f()).forGetter(Trophy::offset),
		ExtraCodecs.VECTOR3F.optionalFieldOf("rotation", new Vector3f()).forGetter(Trophy::rotation),
		Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Trophy::scale),
		CustomBehaviorType.DISPATCH_CODEC.optionalFieldOf("behavior").forGetter(Trophy::clickBehavior),
		Codec.either(Codec.pair(Codec.STRING.fieldOf("key").codec(), Identifier.CODEC.fieldOf("registry").codec()), CompoundTag.CODEC.listOf()).optionalFieldOf("variants", Either.right(new ArrayList<>())).forGetter(Trophy::variants),
		CompoundTag.CODEC.optionalFieldOf("default_variant").forGetter(Trophy::defaultData),
		SoundEvent.DIRECT_CODEC.optionalFieldOf("click_sound_override").forGetter(Trophy::clickSoundOverride)
	).apply(instance, Trophy::new));

	public List<CompoundTag> getVariants(HolderLookup.@Nullable Provider access) {
		if (this.variants.left().isPresent() && access != null) {
			List<CompoundTag> entries = new ArrayList<>();
			Pair<String, Identifier> registryVariant = this.variants.left().get();
			HolderLookup<?> registry = access.lookupOrThrow(ResourceKey.createRegistryKey(registryVariant.getSecond()));
			for (ResourceKey<?> entry : registry.listElementIds().toList()) {
				try {
					CompoundTag formattedTag = Util.make(new CompoundTag(), tag -> tag.putString(registryVariant.getFirst(), entry.identifier().toString()));
					if (!entries.contains(formattedTag)) {
						entries.add(formattedTag);
					}
				} catch (Exception e) {
					OpenBlocksTrophies.LOGGER.error("Something went wrong when trying to fetch variants from a registry!", e);
				}
			}
			return entries;
		}
		return this.variants.right().orElse(new ArrayList<>());
	}

	public static Map<Identifier, Trophy> getTrophies() {
		return TrophyReloadListener.getValidTrophies();
	}

	@SuppressWarnings("unused")
	public static class Builder {
		private boolean replace;
		private final EntityType<?> type;
		private double dropChance = DEFAULT_DROP_CHANCE;
		private Vector3fc offset = new Vector3f();
		private Vector3fc rotation = new Vector3f();
		private float scale = 1.0F;
		@Nullable
		private CustomBehavior clickBehavior = null;
		@Nullable
		private Pair<String, Identifier> registryVariant;
		private List<CompoundTag> variants = new ArrayList<>();
		@Nullable
		private CompoundTag defaultVariant = null;
		@Nullable
		private SoundEvent soundOverride = null;
		public final List<ICondition> loadConditions = new ArrayList<>();

		public Builder(EntityType<?> type) {
			this.type = type;
		}

		public Builder copyFrom(Trophy trophy) {
			this.dropChance = trophy.dropChance();
			this.offset = trophy.offset();
			this.rotation = trophy.rotation();
			this.scale = trophy.scale();
			this.clickBehavior = trophy.clickBehavior().orElse(null);
			this.registryVariant = trophy.variants().left().orElse(null);
			this.variants = new ArrayList<>(trophy.variants().right().orElse(new ArrayList<>()));
			this.defaultVariant = trophy.defaultData().orElse(null);
			this.soundOverride = trophy.clickSoundOverride().orElse(null);
			return this;
		}

		public Trophy.Builder replace() {
			this.replace = true;
			return this;
		}

		public Trophy.Builder setDropChance(double chance) {
			this.dropChance = chance;
			return this;
		}

		public Trophy.Builder setOffset(float xOffset, float yOffset, float zOffset) {
			this.offset = new Vector3f(xOffset, yOffset, zOffset);
			return this;
		}

		public Trophy.Builder setRotation(float xRot, float yRot, float zRot) {
			this.rotation = new Vector3f(xRot, yRot, zRot);
			return this;
		}

		public Trophy.Builder setScale(float scale) {
			this.scale = scale;
			return this;
		}

		public Trophy.Builder setRightClickBehavior(CustomBehavior behavior) {
			this.clickBehavior = behavior;
			return this;
		}

		public Trophy.Builder addVariant(String variantId, String value) {
			CompoundTag tag = new CompoundTag();
			tag.putString(variantId, value);
			this.variants.add(tag);
			return this;
		}

		public Trophy.Builder addVariant(String variantId, int value) {
			CompoundTag tag = new CompoundTag();
			tag.putInt(variantId, value);
			this.variants.add(tag);
			return this;
		}

		public Trophy.Builder addVariant(String variantId, float value) {
			CompoundTag tag = new CompoundTag();
			tag.putFloat(variantId, value);
			this.variants.add(tag);
			return this;
		}

		public Trophy.Builder addVariant(String variantId, double value) {
			CompoundTag tag = new CompoundTag();
			tag.putDouble(variantId, value);
			this.variants.add(tag);
			return this;
		}

		public Trophy.Builder addVariant(String variantId, boolean value) {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean(variantId, value);
			this.variants.add(tag);
			return this;
		}

		public Trophy.Builder addVariant(CompoundTag variant) {
			this.variants.add(variant);
			return this;
		}

		public Trophy.Builder addVariants(List<CompoundTag> variant) {
			this.variants.addAll(variant);
			return this;
		}

		public Trophy.Builder addRegistryVariant(String key, Identifier registryName) {
			this.registryVariant = Pair.of(key, registryName);
			return this;
		}

		public Trophy.Builder addDefaultVariant(CompoundTag variant) {
			this.defaultVariant = variant;
			return this;
		}

		public Trophy.Builder addLoadCondition(ICondition condition) {
			this.loadConditions.add(condition);
			return this;
		}

		public Trophy.Builder addSoundOverride(SoundEvent sound) {
			this.soundOverride = sound;
			return this;
		}

		public Trophy build() {
			return new Trophy(this.replace, this.type, this.dropChance, this.offset, this.rotation, this.scale, Optional.ofNullable(this.clickBehavior), (this.registryVariant != null ? Either.left(this.registryVariant) : Either.right(this.variants)), Optional.ofNullable(this.defaultVariant), Optional.ofNullable(this.soundOverride));
		}
	}
}
