package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.command.GenerateTrophyStubCommand;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.Util;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Locale;
import java.util.function.Consumer;

public class TrophyAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		AdvancementHolder root = Advancement.Builder.advancement().display(
				TrophyItem.loadEntityToTrophy(EntityType.CHICKEN),
				Component.translatable("advancement.obtrophies.root.title"),
				Component.translatable("advancement.obtrophies.root.desc"),
				ResourceLocation.withDefaultNamespace("textures/block/dark_prismarine.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyRegistries.TROPHY_ITEM))
			.save(consumer, "obtrophies:root");

		AdvancementHolder oneTrophy = Advancement.Builder.advancement().parent(root).display(
				TrophyItem.createCyclingTrophy(EntityType.CHICKEN),
				Component.translatable("advancement.obtrophies.one_trophy.title"),
				Component.translatable("advancement.obtrophies.one_trophy.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyRegistries.TROPHY_ITEM))
			.save(consumer, "obtrophies:one_trophy");

		Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadEntityToTrophy(EntityType.WARDEN),
				Component.translatable("advancement.obtrophies.boss_trophy.title"),
				Component.translatable("advancement.obtrophies.boss_trophy.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("has_wither_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.WITHER)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_dragon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.ENDER_DRAGON)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_elder_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.ELDER_GUARDIAN)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_evoker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.EVOKER)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_warden_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.WARDEN)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_ravager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.RAVAGER)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_piglin_brute_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.PIGLIN_BRUTE)).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.requirements(AdvancementRequirements.Strategy.OR)
			.rewards(AdvancementRewards.Builder.experience(100))
			.save(consumer, "obtrophies:boss_trophy");

		Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.AXOLOTL, this.makeIntVariant(4)),
				Component.translatable("advancement.obtrophies.rarest_trophy.title"),
				Component.translatable("advancement.obtrophies.rarest_trophy.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("has_blue_axolotl_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.AXOLOTL, this.makeIntVariant(4))).build()).of(TrophyRegistries.TROPHY_ITEM).build()))
			.rewards(AdvancementRewards.Builder.experience(500))
			.save(consumer, "obtrophies:rarest_trophy");

		this.makeHorses(Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.HORSE, this.makeIntVariant(12)),
				Component.translatable("advancement.obtrophies.all_horse_trophies.title"),
				Component.translatable("advancement.obtrophies.all_horse_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_horse_trophies");

		this.makeCommonFish(Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.TROPICAL_FISH, this.makeIntVariant(TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE))),
				Component.translatable("advancement.obtrophies.all_fish_trophies.title"),
				Component.translatable("advancement.obtrophies.all_fish_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_fish_trophies");

		this.addEveryVanillaMob(Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadEntityToTrophy(EntityType.FOX),
				Component.translatable("advancement.obtrophies.all_vanilla.title"),
				Component.translatable("advancement.obtrophies.all_vanilla.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_vanilla_trophies");
	}

	private CompoundTag makeIntVariant(int variant) {
		return Util.make(new CompoundTag(), tag -> tag.putInt("Variant", variant));
	}

	private Advancement.Builder addEveryVanillaMob(Advancement.Builder builder) {
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals("minecraft") && !OpenBlocksTrophies.UNUSED_TYPES.contains(type)).toList()) {
			Class<?> instance = GenerateTrophyStubCommand.getEntityClass(type);
			if (instance != null && Mob.class.isAssignableFrom(instance)) {
				builder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(type)).build()).of(TrophyRegistries.TROPHY_ITEM).build()));
			}
		}
		return builder;
	}

	private Advancement.Builder makeCommonFish(Advancement.Builder builder) {
		for (TropicalFish.Variant variant : TropicalFish.COMMON_VARIANTS) {
			String fishName = Component.translatable(TropicalFish.getPredefinedName(TropicalFish.COMMON_VARIANTS.indexOf(variant))).getString().toLowerCase(Locale.ROOT).replace(' ', '_');
			builder.addCriterion(fishName, InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.TROPICAL_FISH, this.makeIntVariant(variant.getPackedId()))).build()).of(TrophyRegistries.TROPHY_ITEM).build()));
		}
		return builder;
	}

	private Advancement.Builder makeHorses(Advancement.Builder builder) {
		for (Markings markings : Markings.values()) {
			for (Variant variant : Variant.values()) {
				builder.addCriterion(variant.getSerializedName() + "_" + markings.name().toLowerCase(Locale.ROOT), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().hasComponents(DataComponentPredicate.builder().expect(TrophyRegistries.TROPHY_INFO.get(), new TrophyInfo(EntityType.HORSE, this.makeIntVariant(variant.getId() & 0xFF | markings.getId() << 8 & 0xFF00))).build()).of(TrophyRegistries.TROPHY_ITEM).build()));
			}
		}
		return builder;
	}
}
