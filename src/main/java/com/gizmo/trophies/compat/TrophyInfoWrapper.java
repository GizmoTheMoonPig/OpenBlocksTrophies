package com.gizmo.trophies.compat;

import com.gizmo.trophies.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public record TrophyInfoWrapper(Trophy trophy, int variant) implements IRecipeCategoryExtension {

	public EntityType<?> getTrophyEntity() {
		return this.trophy().getType();
	}

	public double getTrophyDropPercentage() {
		return (TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() >= 0.0D ? TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() : this.trophy().getDropChance()) * 100;
	}

	public ItemStack getTrophyItem() {
		return TrophyItem.loadEntityToTrophy(this.trophy().getType(), this.variant(), false);
	}

	public Map<String, String> getTrophyVariant(RegistryAccess access) {
		if (!this.trophy().getVariants(access).isEmpty()) {
			return this.trophy().getVariants(access).get(this.variant());
		}
		return Map.of();
	}
}
