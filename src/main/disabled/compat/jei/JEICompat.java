package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class JEICompat implements IModPlugin {

	public static final RecipeType<TrophyInfoWrapper> TROPHY = RecipeType.create(OpenBlocksTrophies.MODID, "trophy", TrophyInfoWrapper.class);

	@Override
	public ResourceLocation getPluginUid() {
		return OpenBlocksTrophies.prefix("trophies");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new TrophyCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(TrophyItem.createCyclingTrophy(EntityType.CHICKEN), TROPHY);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<TrophyInfoWrapper> trophies = new LinkedList<>();
		if (!Trophy.getTrophies().isEmpty()) {
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : Trophy.getTrophies().entrySet()) {
				if (trophyEntry.getValue().type() == EntityType.PLAYER || OpenBlocksTrophies.getTrophyDropChance(trophyEntry.getValue()) <= 0.0D) continue;
				if (!trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
					for (CompoundTag variant : trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess())) {
						trophies.add(new TrophyInfoWrapper(trophyEntry.getValue(), variant));
					}
				} else {
					trophies.add(new TrophyInfoWrapper(trophyEntry.getValue(), new CompoundTag()));
				}
			}
		}
		registration.addRecipes(TROPHY, trophies);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(TrophyRegistries.TROPHY_ITEM.get(), TrophyVariantInterpreter.INSTANCE);
	}
}
