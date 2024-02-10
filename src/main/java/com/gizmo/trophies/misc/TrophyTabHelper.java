package com.gizmo.trophies.misc;

import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TrophyTabHelper {
	public static ItemStack makeIcon() {
		return TrophyItem.loadEntityToTrophy(EntityType.CHICKEN, 0, !Trophy.getTrophies().isEmpty());
	}

	public static void getAllTrophies(CreativeModeTab.Output output, HolderLookup.Provider provider, FeatureFlagSet flags) {
		if (!Trophy.getTrophies().isEmpty()) {
			Map<ResourceLocation, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
			sortedTrophies.putAll(Trophy.getTrophies());
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : sortedTrophies.entrySet()) {
				if (trophyEntry.getValue().type().isEnabled(flags)) {
					if (!trophyEntry.getValue().getVariants(provider).isEmpty()) {
						for (int i = 0; i < trophyEntry.getValue().getVariants(provider).size(); i++) {
							output.accept(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().type(), i, false));
						}
					} else {
						output.accept(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().type(), 0, false));
					}
				}
			}
		}
	}
}
