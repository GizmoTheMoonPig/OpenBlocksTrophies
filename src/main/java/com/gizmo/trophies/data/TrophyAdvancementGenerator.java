package com.gizmo.trophies.data;

import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class TrophyAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper helper) {
		Advancement root = Advancement.Builder.advancement().display(
				TrophyItem.loadEntityToTrophy(EntityType.CHICKEN, 0, false),
				Component.translatable("advancement.obtrophies.root.title"),
				Component.translatable("advancement.obtrophies.root.desc"),
				new ResourceLocation("textures/block/dark_prismarine.png"),
				FrameType.TASK, false, false, false)
				.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyRegistries.TROPHY_ITEM.get()))
				.save(consumer, "obtrophies:root");

		Advancement oneTrophy = Advancement.Builder.advancement().parent(root).display(
						TrophyItem.loadEntityToTrophy(EntityType.CHICKEN, 0, true),
						Component.translatable("advancement.obtrophies.one_trophy.title"),
						Component.translatable("advancement.obtrophies.one_trophy.desc"),
						null, FrameType.GOAL, true, true, false)
				.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyRegistries.TROPHY_ITEM.get()))
				.save(consumer, "obtrophies:one_trophy");

		Advancement bossTrophy = Advancement.Builder.advancement().parent(oneTrophy).display(
						TrophyItem.loadEntityToTrophy(EntityType.WARDEN, 0, false),
						Component.translatable("advancement.obtrophies.boss_trophy.title"),
						Component.translatable("advancement.obtrophies.boss_trophy.desc"),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_wither_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WITHER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_dragon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ENDER_DRAGON, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_elder_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ELDER_GUARDIAN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_evoker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.EVOKER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_warden_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WARDEN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_ravager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.RAVAGER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_piglin_brute_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PIGLIN_BRUTE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.requirements(RequirementsStrategy.OR)
				.save(consumer, "obtrophies:boss_trophy");

		Advancement rarestTrophy = Advancement.Builder.advancement().parent(oneTrophy).display(
						TrophyItem.loadEntityToTrophy(EntityType.AXOLOTL, 4, false),
						Component.translatable("advancement.obtrophies.rarest_trophy.title"),
						Component.translatable("advancement.obtrophies.rarest_trophy.desc"),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_blue_axolotl_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.AXOLOTL, 4, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.save(consumer, "obtrophies:rarest_trophy");

		Advancement allHorseTrophies = Advancement.Builder.advancement().parent(oneTrophy).display(
						TrophyItem.loadEntityToTrophy(EntityType.HORSE, 12, false),
						Component.translatable("advancement.obtrophies.all_horse_trophies.title"),
						Component.translatable("advancement.obtrophies.all_horse_trophies.desc"),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_horse_trophy_1", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 0, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_2", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_3", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 2, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_4", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 3, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_5", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 4, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_6", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 5, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_7", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 6, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_8", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 7, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_9", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 8, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_10", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 9, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_11", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 10, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_12", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 11, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_13", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 12, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_14", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 13, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_15", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 14, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_16", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 15, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_17", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 16, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_18", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 17, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_19", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 18, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_20", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 19, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_21", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 20, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_22", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 21, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_23", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 22, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_24", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 23, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_25", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 24, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_26", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 25, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_27", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 26, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_28", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 27, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_29", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 28, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_30", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 29, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_31", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 30, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_32", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 31, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_33", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 32, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_34", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 33, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy_35", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, 34, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.requirements(RequirementsStrategy.AND)
				.save(consumer, "obtrophies:all_horse_trophies");

		Advancement allfishTrophies = Advancement.Builder.advancement().parent(oneTrophy).display(
						TrophyItem.loadEntityToTrophy(EntityType.TROPICAL_FISH, 2, false),
						Component.translatable("advancement.obtrophies.all_fish_trophies.title"),
						Component.translatable("advancement.obtrophies.all_fish_trophies.desc"),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_fish_trophy_1", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 0, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_2", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_3", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 2, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_4", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 3, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_5", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 4, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_6", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 5, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_7", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 6, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_8", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 7, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_9", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 8, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_10", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 9, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_11", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 10, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_12", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 11, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_13", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 12, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_14", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 13, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_15", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 14, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_16", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 15, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_17", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 16, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_18", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 17, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_19", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 18, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_20", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 19, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_21", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 20, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fish_trophy_22", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, 21, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.requirements(RequirementsStrategy.AND)
				.save(consumer, "obtrophies:all_fish_trophies");

		Advancement vanillaTrophies = Advancement.Builder.advancement().parent(oneTrophy).display(
						TrophyItem.loadEntityToTrophy(EntityType.FOX, 0, false),
						Component.translatable("advancement.obtrophies.all_vanilla.title"),
						Component.translatable("advancement.obtrophies.all_vanilla.desc"),
						null, FrameType.CHALLENGE, true, true, false)
				.addCriterion("has_allay_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ALLAY, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_axolotl_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.AXOLOTL, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_bat_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.BAT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_bee_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.BEE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_blaze_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.BLAZE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_camel_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.CAMEL, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_cat_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.CAT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_cave_spider_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.CAVE_SPIDER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_chicken_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.CHICKEN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_cod_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.COD, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_cow_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.COW, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_creeper_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.CREEPER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_dolphin_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.DOLPHIN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_donkey_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.DONKEY, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_drowned_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.DROWNED, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_elder_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ELDER_GUARDIAN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_ender_dragon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ENDER_DRAGON, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_enderman_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ENDERMAN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_endermite_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ENDERMITE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_evoker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.EVOKER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_fox_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.FOX, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_frog_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.FROG, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_ghast_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.GHAST, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_glow_squid_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.GLOW_SQUID, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_goat_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.GOAT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.GUARDIAN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_hoglin_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HOGLIN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_horse_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HORSE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_husk_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.HUSK, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_iron_golem_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.IRON_GOLEM, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_llama_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.LLAMA, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_magma_cube_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.MAGMA_CUBE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_mooshroom_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.MOOSHROOM, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_mule_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.MULE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_ocelot_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.OCELOT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_panda_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PANDA, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_parrot_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PARROT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_phantom_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PHANTOM, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_pig_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PIG, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_piglin_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PIGLIN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_piglin_brute_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PIGLIN_BRUTE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_pillager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PILLAGER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_polar_bear_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.POLAR_BEAR, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_pufferfish_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.PUFFERFISH, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_rabbit_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.RABBIT, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_ravager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.RAVAGER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_salmon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SALMON, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_sheep_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SHEEP, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_shulker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SHULKER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_silverfish_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SILVERFISH, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_skeleton_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SKELETON, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_skeleton_horse_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SKELETON_HORSE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_slime_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SLIME, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_sniffer_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SNIFFER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_snow_golem_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SNOW_GOLEM, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_spider_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SPIDER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_squid_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.SQUID, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_stray_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.STRAY, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_strider_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.STRIDER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_tadpole_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TADPOLE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_trader_llama_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TRADER_LLAMA, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_tropical_fish_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TROPICAL_FISH, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_turtle_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.TURTLE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_vex_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.VEX, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_villager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.VILLAGER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_vindicator_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.VINDICATOR, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_wandering_trader_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WANDERING_TRADER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_warden_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WARDEN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_witch_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WITCH, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_wither_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WITHER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_wither_skeleton_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WITHER_SKELETON, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_wolf_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.WOLF, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_zoglin_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ZOGLIN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_zombie_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ZOMBIE, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_zombie_villager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ZOMBIE_VILLAGER, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.addCriterion("has_zombified_piglin_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasNbt(TrophyItem.createTrophyTag(EntityType.ZOMBIFIED_PIGLIN, -1, false)).of(TrophyRegistries.TROPHY_ITEM.get()).build()))
				.requirements(RequirementsStrategy.AND)
				.save(consumer, "obtrophies:all_vanilla_trophies");
	}
}
