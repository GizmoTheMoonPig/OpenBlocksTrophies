package com.gizmo.trophies.datagen;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.command.GenerateTrophyStubCommand;
import com.gizmo.trophies.init.TrophyItems;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.TrophyInfoPredicate;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Locale;
import java.util.function.Consumer;

public class TrophyAdvancementGenerator implements AdvancementSubProvider {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
		var getter = registries.lookupOrThrow(Registries.ITEM);
		AdvancementHolder root = Advancement.Builder.advancement().display(
				TrophyHelper.loadEntityToTrophy(EntityType.CHICKEN),
				Component.translatable("advancement.obtrophies.root.title"),
				Component.translatable("advancement.obtrophies.root.desc"),
				Identifier.withDefaultNamespace("block/dark_prismarine"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyItems.TROPHY))
			.save(consumer, "obtrophies:root");

		AdvancementHolder oneTrophy = Advancement.Builder.advancement().parent(root).display(
				TrophyHelper.createCyclingTrophy(EntityType.CHICKEN),
				Component.translatable("advancement.obtrophies.one_trophy.title"),
				Component.translatable("advancement.obtrophies.one_trophy.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("has_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(TrophyItems.TROPHY))
			.save(consumer, "obtrophies:one_trophy");

		Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyHelper.loadEntityToTrophy(EntityType.WARDEN),
				Component.translatable("advancement.obtrophies.boss_trophy.title"),
				Component.translatable("advancement.obtrophies.boss_trophy.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("has_wither_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.WITHER)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_dragon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.ENDER_DRAGON)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_elder_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.ELDER_GUARDIAN)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_evoker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.EVOKER)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_warden_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.WARDEN)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_ravager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.RAVAGER)).of(getter, TrophyItems.TROPHY).build()))
			.addCriterion("has_piglin_brute_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(EntityType.PIGLIN_BRUTE)).of(getter, TrophyItems.TROPHY).build()))
			.requirements(AdvancementRequirements.Strategy.OR)
			.rewards(AdvancementRewards.Builder.experience(100))
			.save(consumer, "obtrophies:boss_trophy");

		Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyHelper.loadVariantToTrophy(EntityType.AXOLOTL, this.makeIntVariant(Axolotl.VARIANT_TAG, 4)),
				Component.translatable("advancement.obtrophies.rarest_trophy.title"),
				Component.translatable("advancement.obtrophies.rarest_trophy.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("has_blue_axolotl_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.variantTrophy(EntityType.AXOLOTL, this.makeIntVariant(Axolotl.VARIANT_TAG, 4))).of(getter, TrophyItems.TROPHY).build()))
			.rewards(AdvancementRewards.Builder.experience(500))
			.save(consumer, "obtrophies:rarest_trophy");

		this.makeHorses(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyHelper.loadVariantToTrophy(EntityType.HORSE, this.makeIntVariant("Variant", 12)),
				Component.translatable("advancement.obtrophies.all_horse_trophies.title"),
				Component.translatable("advancement.obtrophies.all_horse_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_horse_trophies");

		this.makeCommonFish(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyHelper.loadVariantToTrophy(EntityType.TROPICAL_FISH, this.makeIntVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE))),
				Component.translatable("advancement.obtrophies.all_fish_trophies.title"),
				Component.translatable("advancement.obtrophies.all_fish_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_fish_trophies");

		this.addEveryVanillaMob(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyHelper.loadEntityToTrophy(EntityType.FOX),
				Component.translatable("advancement.obtrophies.all_vanilla.title"),
				Component.translatable("advancement.obtrophies.all_vanilla.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_vanilla_trophies");
	}

	private CompoundTag makeIntVariant(String key, int variant) {
		return Util.make(new CompoundTag(), tag -> tag.putInt(key, variant));
	}

	private Advancement.Builder addEveryVanillaMob(HolderGetter<Item> getter, Advancement.Builder builder) {
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals("minecraft") && !OpenBlocksTrophies.UNUSED_TYPES.contains(type)).toList()) {
			Class<?> instance = GenerateTrophyStubCommand.getEntityClass(type);
			if (instance != null && Mob.class.isAssignableFrom(instance)) {
				builder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.trophy(type)).of(getter, TrophyItems.TROPHY).build()));
			}
		}
		return builder;
	}

	private Advancement.Builder makeCommonFish(HolderGetter<Item> getter, Advancement.Builder builder) {
		for (TropicalFish.Variant variant : TropicalFish.COMMON_VARIANTS) {
			String fishName = Component.translatable(TropicalFish.getPredefinedName(TropicalFish.COMMON_VARIANTS.indexOf(variant))).getString().toLowerCase(Locale.ROOT).replace(' ', '_');
			builder.addCriterion(fishName, InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.variantTrophy(EntityType.TROPICAL_FISH, this.makeIntVariant("Variant", variant.getPackedId()))).of(getter, TrophyItems.TROPHY).build()));
		}
		return builder;
	}

	private Advancement.Builder makeHorses(HolderGetter<Item> getter, Advancement.Builder builder) {
		for (Markings markings : Markings.values()) {
			for (Variant variant : Variant.values()) {
				builder.addCriterion(variant.getSerializedName() + "_" + markings.name().toLowerCase(Locale.ROOT), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(TrophyInfoPredicate.variantTrophy(EntityType.HORSE, this.makeIntVariant("Variant", variant.getId() & 0xFF | markings.getId() << 8 & 0xFF00))).of(getter, TrophyItems.TROPHY).build()));
			}
		}
		return builder;
	}
}
