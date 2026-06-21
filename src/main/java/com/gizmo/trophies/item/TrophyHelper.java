package com.gizmo.trophies.item;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.init.TrophyItems;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

public class TrophyHelper {

	@Nullable
	public static DisplayTrophy getDisplayTrophy(@Nullable DataComponentMap map) {
		if (map != null && map.has(TrophyComponents.DISPLAY_TROPHY_INFO.get())) {
			return map.get(TrophyComponents.DISPLAY_TROPHY_INFO.get());
		}

		return null;
	}

	@Nullable
	public static Trophy getTrophy(@Nullable TrophyInfo info) {
		if (info != null) {
			Identifier entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(info.type());
			if (Trophy.getTrophies().containsKey(entityKey)) {
				return Trophy.getTrophies().get(entityKey);
			}
		}

		return null;
	}

	public static boolean hasCycleOnTrophy(@Nullable TrophyInfo info) {
		if (info != null) {
			return info.cycling().isPresent();
		}

		return false;
	}

	public static ItemStackTemplate loadEntityToTrophy(EntityType<?> type) {
		return loadVariantToTrophy(type, new CompoundTag());
	}

	public static ItemStackTemplate loadVariantToTrophy(EntityType<?> type, CompoundTag variant) {
		TrophyInfo info = new TrophyInfo(type, variant);
		return new ItemStackTemplate(TrophyItems.TROPHY, DataComponentPatch.builder().set(TrophyComponents.TROPHY_INFO.get(), info).set(DataComponents.RARITY, getTrophyRarity(info)).build());
	}

	public static ItemStackTemplate createCyclingTrophy(EntityType<?> type) {
		TrophyInfo info = new TrophyInfo(type, !Trophy.getTrophies().isEmpty());
		return new ItemStackTemplate(TrophyItems.TROPHY, DataComponentPatch.builder().set(TrophyComponents.TROPHY_INFO.get(), info).set(DataComponents.RARITY, getTrophyRarity(info)).build());
	}

	public static CompoundTag getTrophyVariant(@Nullable TrophyInfo info) {
		if (info != null && info.variant().isPresent()) {
			return info.variant().get();
		}

		return new CompoundTag();
	}

	public static Rarity getTrophyRarity(@Nullable TrophyInfo info) {
		Trophy trophy = getTrophy(info);
		if (trophy != null) {
			if (trophy.type() == EntityType.PLAYER) {
				return Rarity.EPIC;
			} else if (trophy.type().getTags().anyMatch(tag -> tag.equals(Tags.EntityTypes.BOSSES)) || trophy.dropChance() >= Trophy.BOSS_DROP_CHANCE) {
				return Rarity.RARE;
			} else {
				return Rarity.UNCOMMON;
			}
		}
		return Rarity.COMMON;
	}
}
