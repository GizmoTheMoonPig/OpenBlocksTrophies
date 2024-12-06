package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.misc.AddTrophyModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class LootModifierGenerator extends GlobalLootModifierProvider {
	public LootModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void start() {
		add("quest_ram_trophy", new AddTrophyModifier(new LootItemCondition[]{LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("twilightforest", "entities/questing_ram_rewards")).build()}, BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.fromNamespaceAndPath("twilightforest", "quest_ram"))));
	}
}
