package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.TrophyInfoPredicate;
import com.gizmo.trophies.command.GenerateTrophyStubCommand;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.Util;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Locale;
import java.util.function.Consumer;

public class TrophyAdvancementGenerator implements AdvancementSubProvider {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
		var getter = registries.lookupOrThrow(Registries.ITEM);
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
			.addCriterion("has_wither_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.WITHER)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_dragon_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.ENDER_DRAGON)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_elder_guardian_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.ELDER_GUARDIAN)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_evoker_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.EVOKER)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_warden_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.WARDEN)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_ravager_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.RAVAGER)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.addCriterion("has_piglin_brute_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.PIGLIN_BRUTE)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.requirements(AdvancementRequirements.Strategy.OR)
			.rewards(AdvancementRewards.Builder.experience(100))
			.save(consumer, "obtrophies:boss_trophy");

		Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.AXOLOTL, this.makeIntVariant(Axolotl.VARIANT_TAG, 4)),
				Component.translatable("advancement.obtrophies.rarest_trophy.title"),
				Component.translatable("advancement.obtrophies.rarest_trophy.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("has_blue_axolotl_trophy", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.AXOLOTL, this.makeIntVariant(Axolotl.VARIANT_TAG, 4))).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()))
			.rewards(AdvancementRewards.Builder.experience(500))
			.save(consumer, "obtrophies:rarest_trophy");

		this.makeHorses(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.HORSE, this.makeIntVariant("Variant", 12)),
				Component.translatable("advancement.obtrophies.all_horse_trophies.title"),
				Component.translatable("advancement.obtrophies.all_horse_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_horse_trophies");

		this.makeCommonFish(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadVariantToTrophy(EntityType.TROPICAL_FISH, this.makeIntVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE))),
				Component.translatable("advancement.obtrophies.all_fish_trophies.title"),
				Component.translatable("advancement.obtrophies.all_fish_trophies.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			).requirements(AdvancementRequirements.Strategy.AND)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.save(consumer, "obtrophies:all_fish_trophies");

		this.addEveryVanillaMob(getter, Advancement.Builder.advancement().parent(oneTrophy).display(
				TrophyItem.loadEntityToTrophy(EntityType.FOX),
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
				builder.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(type)).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()));
			}
		}
		return builder;
	}

	private Advancement.Builder makeCommonFish(HolderGetter<Item> getter, Advancement.Builder builder) {
		for (TropicalFish.Variant variant : TropicalFish.COMMON_VARIANTS) {
			String fishName = Component.translatable(TropicalFish.getPredefinedName(TropicalFish.COMMON_VARIANTS.indexOf(variant))).getString().toLowerCase(Locale.ROOT).replace(' ', '_');
			builder.addCriterion(fishName, InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.TROPICAL_FISH, this.makeIntVariant("Variant", variant.getPackedId()))).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()));
		}
		return builder;
	}

	private Advancement.Builder makeHorses(HolderGetter<Item> getter, Advancement.Builder builder) {
		for (Markings markings : Markings.values()) {
			for (Variant variant : Variant.values()) {
				builder.addCriterion(variant.getSerializedName() + "_" + markings.name().toLowerCase(Locale.ROOT), InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(TrophyRegistries.TROPHY_INFO_PREDICATE.get(), new TrophyInfoPredicate(EntityType.HORSE, this.makeIntVariant("Variant", variant.getId() & 0xFF | markings.getId() << 8 & 0xFF00))).build()).of(getter, TrophyRegistries.TROPHY_ITEM).build()));
			}
		}
		return builder;
	}
}
