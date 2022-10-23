package com.gizmo.trophies.trophy;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.SyncTrophyConfigsPacket;
import com.gizmo.trophies.TrophyNetworkHandler;
import com.gizmo.trophies.trophy.behaviors.CustomBehavior;
import com.gizmo.trophies.trophy.behaviors.CustomBehaviorRegistry;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
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
import java.util.Map;
import java.util.Objects;

public record Trophy(EntityType<?> type, double dropChance, double verticalOffset, float scale,
					 @Nullable CustomBehavior behavior) {

	public Trophy(EntityType<?> type) {
		this(type, 0.001D, 0.0D, 1.0F, null);
	}

	public Trophy(EntityType<?> type, CustomBehavior behavior) {
		this(type, 0.001D, 0.0D, 1.0F, behavior);
	}

	public Trophy(EntityType<?> type, double verticalOffset, float scale) {
		this(type, 0.001D, verticalOffset, scale, null);
	}

	public Trophy(EntityType<?> type, double verticalOffset, float scale, CustomBehavior behavior) {
		this(type, 0.001D, verticalOffset, scale, behavior);
	}

	public static void reloadTrophies(AddReloadListenerEvent event) {
		event.addListener(new TrophyReloadListener());
	}

	public static void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			if (event.getPlayer() != null) {
				TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer), new SyncTrophyConfigsPacket(getTrophies()));
			} else {
				event.getPlayerList().getPlayers().forEach(player -> TrophyNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncTrophyConfigsPacket(getTrophies())));
			}
		}
	}

	public static Map<ResourceLocation, Trophy> getTrophies() {
		return TrophyReloadListener.getValidTrophies();
	}

	public static Trophy fromJson(JsonObject object) {
		String entityType = GsonHelper.getAsString(object, "entity");
		if (ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityType)) == null) {
			throw new JsonParseException("Entity" + entityType + " defined in Trophy config does not exist!");
		}
		EntityType<?> realEntity = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityType));
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
		return new Trophy(realEntity, dropChance, verticalOffset, scale, behavior);
	}

	public static Trophy fromNetwork(FriendlyByteBuf buf) {
		//behaviors dont happen client side, no need to send them
		return new Trophy(buf.readRegistryId(), buf.readDouble(), buf.readDouble(), buf.readFloat(), null);
	}

	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeRegistryIdUnsafe(ForgeRegistries.ENTITIES, this.type());
		buf.writeDouble(this.dropChance());
		buf.writeDouble(this.verticalOffset());
		buf.writeFloat(this.scale());
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
			jsonobject.add("entity", context.serialize(Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(trophy.type())).toString()));
			jsonobject.add("drop_chance", context.serialize(trophy.dropChance()));
			jsonobject.add("offset", context.serialize(trophy.verticalOffset()));
			jsonobject.add("scale", context.serialize(trophy.scale()));
			if (trophy.behavior() != null) {
				jsonobject.add("behavior", trophy.behavior().serializeBehavior(context));
			}
			return jsonobject;
		}
	}
}
