package com.gizmo.trophies.compat;

import com.gizmo.trophies.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public record TrophyInfoWrapper(Trophy trophy) implements IRecipeCategoryExtension {

	public EntityType<?> getTrophyEntity() {
		return this.trophy().type();
	}

	public double getTrophyDropPercentage() {
		return (TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() >= 0.0D ? TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() : this.trophy().dropChance()) * 100;
	}

	public ItemStack getTrophyItem() {
		return TrophyItem.loadEntityToTrophy(this.trophy().type(), false);
	}
}
