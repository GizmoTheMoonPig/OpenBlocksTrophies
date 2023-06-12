package com.gizmo.trophies.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityCache {

	private static final Map<EntityType<?>, Entity> ENTITY_MAP = new HashMap<>();
	private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

	@Nullable
	public static LivingEntity fetchEntity(EntityType<?> type, @Nullable Level level, Map<String, String> variant) {
		if (level != null && !IGNORED_ENTITIES.contains(type)) {
			Entity entity;
			if (type == EntityType.PLAYER) {
				entity = Minecraft.getInstance().player;
			} else {
				entity = ENTITY_MAP.computeIfAbsent(type, t -> t.create(level));
			}
			if (entity instanceof LivingEntity living) {
				if (TrophyExtraRendering.getRenderMap().containsKey(type)) {
					TrophyExtraRendering.getRenderForEntity(type).createExtraRender(entity);
				}
				if (!variant.isEmpty()) {
					if (entity instanceof VillagerDataHolder villager) {
						variant.forEach((s, s2) -> villager.setVillagerData(new VillagerData(VillagerType.PLAINS, Objects.requireNonNull(Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).get(ResourceLocation.tryParse(s2))), 1)));
					} else {
						CompoundTag tag = new CompoundTag();
						variant.forEach((s, s2) -> TrophyRenderer.convertStringToProperPrimitive(tag, s, s2));
						living.readAdditionalSaveData(tag);
					}
				}
				return living;
			} else {
				IGNORED_ENTITIES.add(type);
				ENTITY_MAP.remove(type);
			}
		}
		return null;
	}

	public static void addEntityToBlacklist(EntityType<?> type) {
		IGNORED_ENTITIES.add(type);
		ENTITY_MAP.remove(type);
	}
}
