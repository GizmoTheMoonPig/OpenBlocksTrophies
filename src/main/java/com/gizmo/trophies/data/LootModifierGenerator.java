package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.AddTrophyModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierGenerator extends GlobalLootModifierProvider {
	public LootModifierGenerator(PackOutput output) {
		super(output, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void start() {
		add("quest_ram_trophy", new AddTrophyModifier(new LootItemCondition[]{LootTableIdCondition.builder(new ResourceLocation("twilightforest", "entities/questing_ram_rewards")).build()}, ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("twilightforest", "quest_ram"))));
	}
}
