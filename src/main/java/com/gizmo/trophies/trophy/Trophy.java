package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.behavior.CustomBehavior;
import com.gizmo.trophies.behavior.CustomTrophyBehaviors;
import com.gizmo.trophies.SyncTrophyConfigsPacket;
import com.gizmo.trophies.TrophyNetworkHandler;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Trophy(EntityType<?> type, double dropChance, double verticalOffset, float scale, Optional<CustomBehavior> clickBehavior, Either<Pair<String, ResourceLocation>, List<CompoundTag>> variants, Optional<CompoundTag> defaultData) {

	public static final Codec<Trophy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(Trophy::type),
			Codec.DOUBLE.optionalFieldOf("drop_chance", 0.001D).forGetter(Trophy::dropChance),
			Codec.DOUBLE.optionalFieldOf("offset", 0.0D).forGetter(Trophy::verticalOffset),
			Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Trophy::scale),
			CustomTrophyBehaviors.CODEC.optionalFieldOf("behavior").forGetter(Trophy::clickBehavior),
			Codec.either(Codec.pair(Codec.STRING.fieldOf("key").codec(), ResourceLocation.CODEC.fieldOf("registry").codec()), CompoundTag.CODEC.listOf()).optionalFieldOf("variants", Either.right(new ArrayList<>())).forGetter(Trophy::variants),
			CompoundTag.CODEC.optionalFieldOf("default_variant").forGetter(Trophy::defaultData)
	).apply(instance, Trophy::new));

	public static void reloadTrophies(AddReloadListenerEvent event) {
		event.addListener(new TrophyReloadListener());
	}

	public static void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer), new SyncTrophyConfigsPacket(getTrophies()));
			OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", getTrophies().size(), event.getPlayer().getDisplayName().getString());
		} else {
			event.getPlayerList().getPlayers().forEach(player -> {
				TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncTrophyConfigsPacket(getTrophies()));
				OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", getTrophies().size(), player.getDisplayName().getString());
			});
		}
	}

	public List<CompoundTag> getVariants(@Nullable RegistryAccess access) {
		if (this.variants.left().isPresent() && access != null) {
			List<CompoundTag> entries = new ArrayList<>();
			Pair<String, ResourceLocation> registryVariant = this.variants.left().get();
			Registry<?> registry = access.registryOrThrow(ResourceKey.createRegistryKey(registryVariant.getSecond()));
			for (Map.Entry<? extends ResourceKey<?>, ?> entry : registry.entrySet()) {
				try {
					entries.add(Util.make(new CompoundTag(), tag -> tag.putString(registryVariant.getFirst(), entry.getKey().location().toString())));
				} catch (ClassCastException e) {
					OpenBlocksTrophies.LOGGER.error("Something went wrong when trying to fetch variants from a registry!");
					e.printStackTrace();
				}
			}
			return entries;
		}
		return this.variants.right().orElse(new ArrayList<>());
	}

	//used for the creative tab since I dont have access to registryaccess
	public List<CompoundTag> getVariants(HolderLookup.Provider access) {
		if (this.variants.left().isPresent()) {
			List<CompoundTag> entries = new ArrayList<>();
			Pair<String, ResourceLocation> registryVariant = this.variants.left().get();
			HolderLookup<?> registry = access.lookupOrThrow(ResourceKey.createRegistryKey(registryVariant.getSecond()));
			for (ResourceKey<?> entry : registry.listElementIds().toList()) {
				try {
					CompoundTag formattedTag = Util.make(new CompoundTag(), tag -> tag.putString(registryVariant.getFirst(), entry.location().toString()));
					if (!entries.contains(formattedTag)) {
						entries.add(formattedTag);
					}
				} catch (ClassCastException e) {
					OpenBlocksTrophies.LOGGER.error("Something went wrong when trying to fetch variants from a registry!");
					e.printStackTrace();
				}
			}
			return entries;
		}
		return this.variants.right().orElse(new ArrayList<>());
	}

	public static TreeMap<ResourceLocation, Trophy> getTrophies() {
		return TrophyReloadListener.getValidTrophies();
	}

	public static class Builder {
		private final EntityType<?> type;
		private double dropChance = 0.001D;
		private double verticalOffset = 0.0D;
		private float scale = 1.0F;
		@Nullable
		private CustomBehavior clickBehavior = null;
		@Nullable
		private Pair<String, ResourceLocation> registryVariant;
		private List<CompoundTag> variants = new ArrayList<>();
		@Nullable
		private CompoundTag defaultVariant = null;

		public Builder(EntityType<?> type) {
			this.type = type;
		}

		public Builder copyFrom(Trophy trophy) {
			this.dropChance = trophy.dropChance();
			this.verticalOffset = trophy.verticalOffset();
			this.scale = trophy.scale();
			this.clickBehavior = trophy.clickBehavior().orElse(null);
			this.registryVariant = trophy.variants().left().orElse(null);
			this.variants = trophy.variants().right().orElse(new ArrayList<>());
			this.defaultVariant = trophy.defaultData().orElse(null);
			return this;
		}

		public Trophy.Builder setDropChance(double chance) {
			this.dropChance = chance;
			return this;
		}

		public Trophy.Builder setVerticalOffset(double offset) {
			this.verticalOffset = offset;
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

		public Trophy.Builder addRegistryVariant(String key, ResourceLocation registryName) {
			this.registryVariant = Pair.of(key, registryName);
			return this;
		}

		public Trophy.Builder addDefaultVariant(CompoundTag variant) {
			this.defaultVariant = variant;
			return this;
		}

		public Trophy build() {
			return new Trophy(this.type, this.dropChance, this.verticalOffset, this.scale, Optional.ofNullable(this.clickBehavior), this.registryVariant != null ? Either.left(this.registryVariant) : Either.right(this.variants) , Optional.ofNullable(this.defaultVariant));
		}
	}
}
