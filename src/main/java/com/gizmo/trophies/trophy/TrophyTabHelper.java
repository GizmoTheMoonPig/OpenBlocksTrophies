package com.gizmo.trophies.trophy;

import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TrophyTabHelper {
	public static ItemStack makeIcon() {
		return TrophyItem.loadEntityToTrophy(EntityType.CHICKEN, 0, !Trophy.getTrophies().isEmpty());
	}

	public static void getAllTrophies(CreativeModeTab.Output output) {
		if (!Trophy.getTrophies().isEmpty()) {
			Map<ResourceLocation, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
			sortedTrophies.putAll(Trophy.getTrophies());
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : sortedTrophies.entrySet()) {
				if (!trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
					for (int i = 0; i < trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).size(); i++) {
						output.accept(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().getType(), i, false));
					}
				} else {
					output.accept(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().getType(), 0, false));
				}
			}
		}
	}
}
