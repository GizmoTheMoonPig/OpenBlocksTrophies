package com.gizmo.trophies.compat.emi;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
	public static final EmiRecipeCategory TROPHY = new EmiRecipeCategory(OpenBlocksTrophies.prefix("trophy"), EmiStack.of(TrophyItem.createCyclingTrophy(EntityType.CHICKEN)));

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(TROPHY);

		if (!Trophy.getTrophies().isEmpty()) {
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : Trophy.getTrophies().entrySet()) {
				if (trophyEntry.getValue().type() == EntityType.PLAYER || OpenBlocksTrophies.getTrophyDropChance(trophyEntry.getValue()) <= 0.0D) continue;
				if (!trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
					for (CompoundTag tag : trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess())) {
						registry.addRecipe(new EmiTrophyRecipe(ResourceLocation.parse(tag.get(tag.getAllKeys().stream().toList().getFirst()).getAsString() + "_" + trophyEntry.getKey().toString().replace(':', '_')), trophyEntry.getValue(), tag));
					}
				} else {
					registry.addRecipe(new EmiTrophyRecipe(trophyEntry.getKey(), trophyEntry.getValue(), new CompoundTag()));
				}
			}
		}
	}
}
