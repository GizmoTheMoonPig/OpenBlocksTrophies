package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.init.TrophyComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class TrophyVariantInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final TrophyVariantInterpreter INSTANCE = new TrophyVariantInterpreter();

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return ingredient.get(TrophyComponents.TROPHY_INFO);
	}
}
