package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.SyncTrophyConfigsPacket;
import com.gizmo.trophies.TrophyNetworkHandler;
import com.gizmo.trophies.trophy.behaviors.CustomBehavior;
import com.gizmo.trophies.trophy.behaviors.CustomBehaviorRegistry;
import com.google.gson.*;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class Trophy {

	private final EntityType<?> type;
	private final double dropChance;
	private final double verticalOffset;
	private final float scale;
	@Nullable
	private final CustomBehavior clickBehavior;
	private final List<Map<String, String>> variants;
	@Nullable
	private final Pair<String, ResourceKey<? extends Registry<?>>> registry;

	private Trophy(EntityType<?> type, double dropChance, double verticalOffset, float scale, @Nullable CustomBehavior behavior, List<Map<String, String>> variants, @Nullable Pair<String, ResourceKey<? extends Registry<?>>> registry) {
		this.type = type;
		this.dropChance = dropChance;
		this.verticalOffset = verticalOffset;
		this.scale = scale;
		this.clickBehavior = behavior;
		this.variants = variants;
		this.registry = registry;
	}

	public EntityType<?> getType() {
		return this.type;
	}

	public float getScale() {
		return this.scale;
	}

	public double getDropChance() {
		return this.dropChance;
	}

	public double getVerticalOffset() {
		return this.verticalOffset;
	}

	@Nullable
	public CustomBehavior getClickBehavior() {
		return this.clickBehavior;
	}

	public List<Map<String, String>> getVariants(@Nullable RegistryAccess access) {
		if (this.registry != null && access != null) {
			List<Map<String, String>> entries = new ArrayList<>();
			Registry<?> registry = access.registryOrThrow(this.registry.right());
			for (Map.Entry<?, ?> entry : registry.entrySet()) {
				entries.add(Map.of(this.registry.left(), ((ResourceKey<?>)entry.getKey()).location().toString()));
			}
			return entries;
		}
		return this.variants;
	}

	public void mergeVariantsFromOtherTrophy(Trophy trophy) {
		if (!trophy.variants.isEmpty()) {
			this.variants.addAll(trophy.variants);
		}
	}

	public static void reloadTrophies(AddReloadListenerEvent event) {
		event.addListener(new TrophyReloadListener());
	}

	public static void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			if (event.getPlayer() != null) {
				splitMap(getTrophies(), 50).forEach(splitMap -> {
					TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer), new SyncTrophyConfigsPacket(splitMap));
					OpenBlocksTrophies.LOGGER.debug("sent a group of {} trophies to client", splitMap.size());
				});
			} else {
				event.getPlayerList().getPlayers().forEach(player -> splitMap(getTrophies(), 50).forEach(splitMap -> {
					TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer), new SyncTrophyConfigsPacket(splitMap));
					OpenBlocksTrophies.LOGGER.debug("sent a group of {} trophies to player {}", splitMap.size(), event.getPlayer().getDisplayName());
				}));
			}
		}
	}

	private static <K, V> List<SortedMap<K, V>> splitMap(TreeMap<K, V> map, int size) {
		List<K> keys = new ArrayList<>(map.keySet());
		List<SortedMap<K, V>> parts = new ArrayList<>();
		final int listSize = map.size();
		for (int i = 0; i < listSize; i += size) {
			if (i + size < listSize) {
				parts.add(map.subMap(keys.get(i), keys.get(i + size)));
			} else {
				parts.add(map.tailMap(keys.get(i)));
			}
		}
		return parts;
	}

	public static TreeMap<ResourceLocation, Trophy> getTrophies() {
		return TrophyReloadListener.getValidTrophies();
	}

	public static Trophy fromJson(JsonObject object) {
		String entityType = GsonHelper.getAsString(object, "entity");
		if (ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(entityType)) == null) {
			throw new JsonParseException("Entity" + entityType + " defined in Trophy config does not exist!");
		}
		EntityType<?> realEntity = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(entityType));
		double dropChance = GsonHelper.getAsDouble(object, "drop_chance", 0.001D);
		double verticalOffset = GsonHelper.getAsDouble(object, "offset", 0.0D);
		float scale = GsonHelper.getAsFloat(object, "scale", 1.0F);
		CustomBehavior behavior = null;
		if (object.has("behavior")) {
			try {
				JsonObject bObject = GsonHelper.convertToJsonObject(object.get("behavior"), "behavior");
				CustomBehavior fetchedBehavior = CustomBehaviorRegistry.getBehavior(Objects.requireNonNull(ResourceLocation.tryParse(GsonHelper.getAsString(bObject, "type"))));
				behavior = fetchedBehavior.fromJson(bObject);
			} catch (Exception e) {
				OpenBlocksTrophies.LOGGER.error("Could not fetch custom behavior for trophy {}, setting to null", entityType, e);
			}
		}
		Pair<String, ResourceKey<? extends Registry<?>>> registry = null;
		List<Map<String, String>> variants = new ArrayList<>();
		if (object.has("variants")) {
			JsonArray array = GsonHelper.getAsJsonArray(object, "variants");
			array.forEach(jsonElement -> {
				Map<String, String> variant = new HashMap<>();
				jsonElement.getAsJsonObject().entrySet().forEach(entry -> variant.put(entry.getKey(), entry.getValue().getAsString()));
				variants.add(variant);
			});
		} else if (object.has("variant_registry")) {
			JsonObject vObject = GsonHelper.convertToJsonObject(object.get("variant_registry"), "variant_registry");
			registry = Pair.of(GsonHelper.getAsString(vObject, "key"), ResourceKey.createRegistryKey(Objects.requireNonNull(ResourceLocation.tryParse(GsonHelper.getAsString(vObject, "registry")))));
		}
		return new Trophy(Objects.requireNonNull(realEntity), dropChance, verticalOffset, scale, behavior, variants, registry);
	}

	//right click behaviors are done entirely server-side, no need to send them to the client
	public static Trophy fromNetwork(FriendlyByteBuf buf) {
		return new Trophy(buf.readRegistryId(), buf.readDouble(), buf.readDouble(), buf.readFloat(), null, buf.readList(buf1 -> buf1.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf)), handleRegistryPair(buf.readUtf(), buf.readUtf()));
	}

	@Nullable
	private static Pair<String, ResourceKey<? extends Registry<?>>> handleRegistryPair(String key, String registry) {
		return key.isEmpty() || registry.isEmpty() ? null : Pair.of(key, ResourceKey.createRegistryKey(Objects.requireNonNull(ResourceLocation.tryParse(registry))));
	}

	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeRegistryId(ForgeRegistries.ENTITY_TYPES, this.type);
		buf.writeDouble(this.dropChance);
		buf.writeDouble(this.verticalOffset);
		buf.writeFloat(this.scale);
		buf.writeCollection(this.variants, (buf1, stringStringMap) -> buf1.writeMap(stringStringMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf));
		buf.writeUtf(this.registry != null ? this.registry.left() : "");
		buf.writeUtf(this.registry != null ? this.registry.right().location().toString() : "");
	}

	public static class Serializer implements JsonSerializer<Trophy>, JsonDeserializer<Trophy> {

		@Override
		public Trophy deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = GsonHelper.convertToJsonObject(json, "trophy");
			return Trophy.fromJson(object);
		}

		@Override
		public JsonElement serialize(Trophy trophy, Type type, JsonSerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("entity", context.serialize(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type)).toString()));
			jsonobject.add("drop_chance", context.serialize(trophy.dropChance));
			jsonobject.add("offset", context.serialize(trophy.verticalOffset));
			jsonobject.add("scale", context.serialize(trophy.scale));
			if (trophy.clickBehavior != null) {
				jsonobject.add("behavior", trophy.clickBehavior.serializeBehavior(context));
			}

			if (!trophy.variants.isEmpty()) {
				JsonArray array = new JsonArray();
				trophy.variants.forEach(map -> {
					List<Pair<String, String>> variants = map.entrySet().stream()
							.map(e -> Pair.of(e.getKey(), e.getValue()))
							.sorted(Comparator.comparing(Pair::left)) // Compare only the variant names
							.toList();

					JsonObject entryObject = new JsonObject();
					variants.forEach(entry -> entryObject.add(entry.left(), context.serialize(entry.right())));
					array.add(entryObject);
				});

				jsonobject.add("variants", array);
			} else if (trophy.registry != null) {
				JsonObject object = new JsonObject();
				object.add("key", context.serialize(trophy.registry.left()));
				object.add("registry", context.serialize(trophy.registry.right().location().toString()));
				jsonobject.add("variant_registry", object);
			}
			return jsonobject;
		}
	}

	public static class Builder {
		private final EntityType<?> type;
		private double dropChance = 0.001D;
		private double verticalOffset = 0.0D;
		private float scale = 1.0F;
		@Nullable
		private CustomBehavior clickBehavior = null;
		private final List<Map<String, String>> variants = new ArrayList<>();
		@Nullable
		private Pair<String, ResourceKey<? extends Registry<?>>> registry = null;

		public Builder(EntityType<?> type) {
			this.type = type;
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
			this.variants.add(Map.of(variantId, value));
			return this;
		}

		public Trophy.Builder addVariant(Map<String, String> variant) {
			this.variants.add(variant);
			return this;
		}

		public Trophy.Builder addRegistry(String identifier, ResourceKey<? extends Registry<?>> registry) {
			this.registry = Pair.of(identifier, registry);
			return this;
		}

		public Trophy build() {
			return new Trophy(this.type, this.dropChance, this.verticalOffset, this.scale, this.clickBehavior, this.variants, this.registry);
		}
	}
}
