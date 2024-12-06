package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TrophyRegistries;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class TrophyVariantInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
	public static final TrophyVariantInterpreter INSTANCE = new TrophyVariantInterpreter();

	@Override
	public String apply(ItemStack stack, UidContext context) {
		if (stack.getComponentsPatch().isEmpty()) {
			return IIngredientSubtypeInterpreter.NONE;
		}
		TrophyInfo info = stack.getOrDefault(TrophyRegistries.TROPHY_INFO, TrophyInfo.DEFAULT);
		String itemDescriptionId = stack.getItem().getDescriptionId();
		String entityDescriptionId = info.type().getDescriptionId();
		CompoundTag tag = info.variant().orElse(new CompoundTag());
		List<String> strings = new ArrayList<>();
		for (String key : tag.getAllKeys()) {
			var value = tag.get(key);

			if (value != null) {
				strings.add(value.getAsString().replace(':', '_'));
			}
		}

		StringJoiner joiner = new StringJoiner(",", "[", "]");
		strings.sort(null);
		for (String s : strings) {
			joiner.add(s);
		}
		return itemDescriptionId + "." + entityDescriptionId + ".variant." + joiner;
	}
}
