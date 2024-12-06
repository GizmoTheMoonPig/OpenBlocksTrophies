package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record TrophyInfoWrapper(Trophy trophy, CompoundTag variant) implements IRecipeCategoryExtension<TrophyInfoWrapper> {

	public EntityType<?> getTrophyEntity() {
		return this.trophy().type();
	}

	public ItemStack getTrophyItem() {
		return TrophyItem.loadVariantToTrophy(this.trophy().type(), this.variant());
	}

	public Optional<CompoundTag> getDefaultTrophyVariant() {
		return this.trophy().defaultData();
	}
}
