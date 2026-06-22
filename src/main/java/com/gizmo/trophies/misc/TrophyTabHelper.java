package com.gizmo.trophies.misc;

import com.gizmo.trophies.client.CreativeModeVariantToggle;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLLoader;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TrophyTabHelper {

	public static ItemStack makeIcon() {
		return TrophyHelper.createCyclingTrophy(EntityTypes.CHICKEN).create();
	}

	public static void getAllTrophies(CreativeModeTab.Output output, HolderLookup.Provider provider, FeatureFlagSet flags, boolean showVariants) {
		if (!Trophy.getTrophies().isEmpty()) {
			Map<Identifier, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
			sortedTrophies.putAll(Trophy.getTrophies());
			for (Map.Entry<Identifier, Trophy> trophyEntry : sortedTrophies.entrySet()) {
				if (trophyEntry.getValue().type().isEnabled(flags)) {
					if (!trophyEntry.getValue().getVariants(provider).isEmpty() && showVariants) {
						trophyEntry.getValue().getVariants(provider).forEach(tag -> output.accept(TrophyHelper.loadVariantToTrophy(trophyEntry.getValue().type(), tag).create()));
					} else {
						output.accept(TrophyHelper.loadEntityToTrophy(trophyEntry.getValue().type()).create());
					}
				}
			}
		}
	}

	public static boolean shouldShowVariants() {
		if (FMLLoader.getCurrent().getDist().isClient()) {
			return CreativeModeVariantToggle.showVariants == null || CreativeModeVariantToggle.showVariants.isSelected();
		}
		return true;
	}
}
