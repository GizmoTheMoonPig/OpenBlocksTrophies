package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.misc.AddTrophyModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class LootModifierGenerator extends GlobalLootModifierProvider {
	public LootModifierGenerator(PackOutput output) {
		super(output, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void start() {
		add("quest_ram_trophy", new AddTrophyModifier(new LootItemCondition[]{LootTableIdCondition.builder(new ResourceLocation("twilightforest", "entities/questing_ram_rewards")).build()}, BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("twilightforest", "quest_ram"))));
	}
}
