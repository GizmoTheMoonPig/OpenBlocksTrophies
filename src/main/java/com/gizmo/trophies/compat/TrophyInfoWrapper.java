package com.gizmo.trophies.compat;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TrophyInfoWrapper(Trophy trophy, int variant) implements IRecipeCategoryExtension {

	public EntityType<?> getTrophyEntity() {
		return this.trophy().type();
	}

	public double getTrophyDropPercentage() {
		return OpenBlocksTrophies.getTrophyDropChance(this.trophy()) * 100;
	}

	public ItemStack getTrophyItem() {
		return TrophyItem.loadEntityToTrophy(this.trophy().type(), this.variant(), false);
	}

	@Nullable
	public CompoundTag getTrophyVariant() {
		if (!this.trophy().getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
			return this.trophy().getVariants(Minecraft.getInstance().level.registryAccess()).get(this.variant());
		}
		return null;
	}

	public Optional<CompoundTag> getDefaultTrophyVariant() {
		return this.trophy().defaultData();
	}
}
