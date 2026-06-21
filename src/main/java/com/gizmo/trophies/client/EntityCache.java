package com.gizmo.trophies.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EntityCache {

	private static final Logger LOGGER = LoggerFactory.getLogger("Entity Trophy Cache");
	private static final Map<EntityType<?>, Entity> ENTITY_MAP = new WeakHashMap<>();
	private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

	@Nullable
	public static Entity fetchEntity(EntityType<?> type, @Nullable Level level, CompoundTag variant, Optional<CompoundTag> defaultVariant) {
		if (level != null && !IGNORED_ENTITIES.contains(type)) {
			Entity entity = null;
			if (type == EntityType.PLAYER) {
				type = EntityType.MANNEQUIN;
			}
			try {
				entity = ENTITY_MAP.computeIfAbsent(type, t -> {
					long start = System.currentTimeMillis();
					CompoundTag tag = new CompoundTag();
					tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(t).toString());
					Entity created = EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.COMMAND, EntityProcessor.NOP);
					if (created != null) {
						created.setYRot(0.0F);
						created.setYHeadRot(0.0F);
						created.setYBodyRot(0.0F);
						created.setOldRot();
						created.setId(0);
						created.setCustomNameVisible(false);
						created.needsSync = false;
						created.hurtMarked = false;
						if (created instanceof Mob mob) {
							mob.setNoAi(true);
						}
						//pain
						//eye glowing isnt stored via NBT and I want the trophy to glow
						if (created instanceof Creaking creaking) {
							creaking.setIsActive(true);
						}
					}
					LOGGER.trace("{} creation took {}ms", t.getDescription().getString(), System.currentTimeMillis() - start);
					return created;
				});
			} catch (Exception e) {
				LOGGER.error("Failed to cache a render for entity {}", type.getDescriptionId(), e);
				addEntityToBlacklist(type);
			}
			CompoundTag tag = new CompoundTag();
			defaultVariant.ifPresent(tag1 -> tag1.entrySet().forEach(entry -> tag.put(entry.getKey(), entry.getValue())));
			if (!variant.isEmpty()) {
				if (entity instanceof VillagerDataHolder villager) {
					variant.keySet().forEach(s -> villager.setVillagerData(villager.getVillagerData().withProfession(level.registryAccess(), ResourceKey.create(Registries.VILLAGER_PROFESSION, Identifier.parse(variant.getStringOr(s, ""))))));
				} else {
					variant.keySet().forEach(s -> tag.put(s, Objects.requireNonNull(variant.get(s))));
				}
			}
			if (!tag.isEmpty() && entity != null) {
				try (ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
					entity.load(TagValueInput.create(collector, entity.registryAccess(), tag));
				}
			}
			return entity;
		}
		return null;
	}

	public static void addEntityToBlacklist(EntityType<?> type) {
		IGNORED_ENTITIES.add(type);
		ENTITY_MAP.remove(type);
	}
}
