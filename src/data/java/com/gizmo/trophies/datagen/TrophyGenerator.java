package com.gizmo.trophies.datagen;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.behavior.*;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TrophyGenerator extends TrophyProvider {
	public TrophyGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void createTrophies(HolderLookup.Provider provider) {
		this.makeTrophy(new Trophy.Builder(EntityTypes.PLAYER));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WOLF).setScale(1.25F)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", "minecraft:pale")))
			.addRegistryVariant("variant", Registries.WOLF_VARIANT.identifier()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CHICKEN)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", TemperatureVariants.TEMPERATE.toString())))
			.addRegistryVariant("variant", Registries.CHICKEN_VARIANT.identifier())
			.setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.EGG, 10000, SoundEvents.CHICKEN_EGG)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.COW)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", TemperatureVariants.TEMPERATE.toString())))
			.addRegistryVariant("variant", Registries.COW_VARIANT.identifier())
			.setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), SoundEvents.COW_MILK)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CREEPER).setRightClickBehavior(new ExplosionBehavior(2, false))
			.addVariant("powered", false)
			.addVariant("powered", true));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SKELETON).setRightClickBehavior(new ShootProjectileBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOMBIFIED_PIGLIN).setRightClickBehavior(new ItemDropBehavior(Items.GOLD_NUGGET, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.BAT).setScale(1.4F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOMBIE));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WITCH).setScale(0.9F).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 70, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.VILLAGER).addRegistryVariant("profession", Registries.VILLAGER_PROFESSION.identifier()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.OCELOT));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SHEEP)
			.addVariant("Color", DyeColor.WHITE.getId())
			.addVariant("Color", DyeColor.ORANGE.getId())
			.addVariant("Color", DyeColor.MAGENTA.getId())
			.addVariant("Color", DyeColor.LIGHT_BLUE.getId())
			.addVariant("Color", DyeColor.YELLOW.getId())
			.addVariant("Color", DyeColor.LIME.getId())
			.addVariant("Color", DyeColor.PINK.getId())
			.addVariant("Color", DyeColor.GRAY.getId())
			.addVariant("Color", DyeColor.LIGHT_GRAY.getId())
			.addVariant("Color", DyeColor.CYAN.getId())
			.addVariant("Color", DyeColor.PURPLE.getId())
			.addVariant("Color", DyeColor.BLUE.getId())
			.addVariant("Color", DyeColor.BROWN.getId())
			.addVariant("Color", DyeColor.GREEN.getId())
			.addVariant("Color", DyeColor.RED.getId())
			.addVariant("Color", DyeColor.BLACK.getId())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Color", DyeColor.WHITE.getId()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.BLAZE).setRightClickBehavior(new PlayerSetFireBehavior(4)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SILVERFISH).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SPIDER));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CAVE_SPIDER).setRightClickBehavior(new MobEffectBehavior(MobEffects.POISON, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SLIME).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.GHAST).setOffset(0.0F, 0.35F, 0.0F).setScale(0.25F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ENDERMAN).setScale(0.75F).setRightClickBehavior(new ShootEnderPearlBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.MAGMA_CUBE).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SQUID).setOffset(0.0F, 0.5F, 0.0F).setRightClickBehavior(new PlaceBlockBehavior(Blocks.WATER, PlaceBlockBehavior.PlacementMethod.ABOVE)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.MOOSHROOM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.RED_MUSHROOM, PlaceBlockBehavior.PlacementMethod.AROUND))
			.addVariant("Type", MushroomCow.Variant.RED.getSerializedName())
			.addVariant("Type", MushroomCow.Variant.BROWN.getSerializedName())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("Type", MushroomCow.Variant.RED.getSerializedName()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.IRON_GOLEM).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SNOW_GOLEM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.SNOW, PlaceBlockBehavior.PlacementMethod.AROUND))
			.addVariant("Pumpkin", true)
			.addVariant("Pumpkin", false)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putBoolean("Pumpkin", true))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PIG)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", TemperatureVariants.TEMPERATE.toString())))
			.addRegistryVariant("variant", Registries.PIG_VARIANT.identifier())
			.setRightClickBehavior(new ItemDropBehavior(Items.PORKCHOP, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ENDERMITE).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.GUARDIAN).setRightClickBehavior(new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.RABBIT).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.CARROT, 20000))
			.addVariant("RabbitType", Rabbit.Variant.BROWN.id())
			.addVariant("RabbitType", Rabbit.Variant.WHITE.id())
			.addVariant("RabbitType", Rabbit.Variant.BLACK.id())
			.addVariant("RabbitType", Rabbit.Variant.WHITE_SPLOTCHED.id())
			.addVariant("RabbitType", Rabbit.Variant.GOLD.id())
			.addVariant("RabbitType", Rabbit.Variant.SALT.id())
			.addVariant("RabbitType", Rabbit.Variant.EVIL.id())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("RabbitType", Rabbit.Variant.BROWN.id()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.POLAR_BEAR).setRightClickBehavior(new ItemDropBehavior(Items.COD, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SHULKER).setRightClickBehavior(new MobEffectBehavior(MobEffects.LEVITATION, 100, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000))
			.addVariant("Variant", this.setHorseVariant(Variant.WHITE, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.CREAMY, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.CHESTNUT, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.BROWN, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.BLACK, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.GRAY, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.DARK_BROWN, Markings.NONE))
			.addVariant("Variant", this.setHorseVariant(Variant.WHITE, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.CREAMY, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.CHESTNUT, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.BROWN, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.BLACK, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.GRAY, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.DARK_BROWN, Markings.WHITE))
			.addVariant("Variant", this.setHorseVariant(Variant.WHITE, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.CREAMY, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.CHESTNUT, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.BROWN, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.BLACK, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.GRAY, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.DARK_BROWN, Markings.WHITE_FIELD))
			.addVariant("Variant", this.setHorseVariant(Variant.WHITE, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.CREAMY, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.CHESTNUT, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.BROWN, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.BLACK, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.GRAY, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.DARK_BROWN, Markings.WHITE_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.WHITE, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.CREAMY, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.CHESTNUT, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.BROWN, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.BLACK, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.GRAY, Markings.BLACK_DOTS))
			.addVariant("Variant", this.setHorseVariant(Variant.DARK_BROWN, Markings.BLACK_DOTS))
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Variant", this.setHorseVariant(Variant.WHITE, Markings.NONE)))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SKELETON_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.BONE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOMBIE_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.DONKEY).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.MULE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.LLAMA).setScale(0.9F).setRightClickBehavior(new ShootLlamaSpitBehavior())
			.addVariant("Variant", Llama.Variant.CREAMY.getId())
			.addVariant("Variant", Llama.Variant.WHITE.getId())
			.addVariant("Variant", Llama.Variant.BROWN.getId())
			.addVariant("Variant", Llama.Variant.GRAY.getId())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Variant", Llama.Variant.CREAMY.getId()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ELDER_GUARDIAN).setScale(0.5F).setRightClickBehavior(new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WITHER_SKELETON).setRightClickBehavior(new ItemDropBehavior(Items.WITHER_SKELETON_SKULL, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.STRAY).setRightClickBehavior(new ShootProjectileBehavior(new ItemStackTemplate(Items.TIPPED_ARROW, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOWNESS)).build()), 1, true, Optional.of(SoundEvents.ARROW_SHOOT))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.HUSK).setRightClickBehavior(new ItemDropBehavior(Items.FEATHER, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOMBIE_VILLAGER).addRegistryVariant("profession", Registries.VILLAGER_PROFESSION.identifier()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.EVOKER).setRightClickBehavior(new TotemOfUndyingEffectBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.VEX).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.VINDICATOR).setRightClickBehavior(new ItemDropBehavior(Items.IRON_AXE, 20000)));

		//newbies - 1.12+ mobs
		this.makeTrophy(new Trophy.Builder(EntityTypes.PARROT).setScale(1.75F)
			.addVariant("Variant", Parrot.Variant.RED_BLUE.getId())
			.addVariant("Variant", Parrot.Variant.BLUE.getId())
			.addVariant("Variant", Parrot.Variant.GREEN.getId())
			.addVariant("Variant", Parrot.Variant.YELLOW_BLUE.getId())
			.addVariant("Variant", Parrot.Variant.GRAY.getId())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Variant", Parrot.Variant.RED_BLUE.getId()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ILLUSIONER).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 100, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.COD).setScale(1.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SALMON).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.TROPICAL_FISH).setScale(2.0F)
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED))
			.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED))
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Variant", TropicalFish.packVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE)))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PUFFERFISH).setScale(2.0F).setRightClickBehavior(new MobEffectBehavior(MobEffects.NAUSEA, 100, 0))
			.addVariant("PuffState", 0)
			.addVariant("PuffState", 1)
			.addVariant("PuffState", 2)
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("PuffState", 2))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.DOLPHIN).setRightClickBehavior(new MobEffectBehavior(MobEffects.DOLPHINS_GRACE, 300, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.DROWNED).setRightClickBehavior(new ItemDropBehavior(Items.NAUTILUS_SHELL, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PHANTOM).setRightClickBehavior(new ItemDropBehavior(Items.PHANTOM_MEMBRANE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.TURTLE));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CAT).setScale(1.25F).setRightClickBehavior(new PullFromLootTableBehavior(BuiltInLootTables.CAT_MORNING_GIFT, 20000))
			.addRegistryVariant("variant", Registries.CAT_VARIANT.identifier())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", "minecraft:red"))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.FOX).setRightClickBehavior(new ItemDropBehavior(Items.SWEET_BERRIES, 20000))
			.addVariant("Type", Fox.Variant.RED.getSerializedName())
			.addVariant("Type", Fox.Variant.SNOW.getSerializedName())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("Type", Fox.Variant.RED.getSerializedName()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PANDA).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.BAMBOO, 20000))
			.addVariant(this.buildPandaVariant(Panda.Gene.NORMAL.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.LAZY.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.WORRIED.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.PLAYFUL.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.AGGRESSIVE.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.WEAK.getSerializedName()))
			.addVariant(this.buildPandaVariant(Panda.Gene.BROWN.getSerializedName()))
			.addDefaultVariant(this.buildPandaVariant(Panda.Gene.NORMAL.getSerializedName())));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PILLAGER).setRightClickBehavior(new ShootProjectileBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.RAVAGER).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.SADDLE, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.TRADER_LLAMA).setRightClickBehavior(new ShootLlamaSpitBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WANDERING_TRADER).setRightClickBehavior(new ItemDropBehavior(Items.EMERALD, 10000, SoundEvents.WANDERING_TRADER_YES)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.BEE).setScale(1.5F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.HONEY_BOTTLE, 0), 20000, SoundEvents.BOTTLE_FILL)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.HOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.LEATHER, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PIGLIN).setRightClickBehavior(new ClickWithItemBehavior(provider.getOrThrow(Tags.Items.INGOTS_GOLD), true, new PullFromLootTableBehavior(BuiltInLootTables.PIGLIN_BARTERING, 0), 200, SoundEvents.PIGLIN_ADMIRING_ITEM)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.STRIDER));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 10000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PIGLIN_BRUTE).setRightClickBehavior(new ItemDropBehavior(Items.GOLDEN_AXE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.AXOLOTL).setScale(1.25F).addLoadCondition(new NotCondition(new ModLoadedCondition("mavm")))
			.addVariant("Variant", Axolotl.Variant.LUCY.getId())
			.addVariant("Variant", Axolotl.Variant.WILD.getId())
			.addVariant("Variant", Axolotl.Variant.GOLD.getId())
			.addVariant("Variant", Axolotl.Variant.CYAN.getId())
			.addVariant("Variant", Axolotl.Variant.BLUE.getId())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putInt("Variant", Axolotl.Variant.LUCY.getId()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.GLOW_SQUID).setOffset(0.0F, 0.5F, 0.0F).setRightClickBehavior(new MobEffectBehavior(MobEffects.GLOWING, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.GOAT).setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), 0, SoundEvents.GOAT_MILK)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ALLAY).setScale(1.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.FROG).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.SLIME_BALL, 20000))
			.addRegistryVariant("variant", Registries.FROG_VARIANT.identifier())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", TemperatureVariants.TEMPERATE.toString()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.TADPOLE).setScale(2.0F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WARDEN).setScale(0.75F).setRightClickBehavior(new MobEffectBehavior(MobEffects.DARKNESS, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.WITHER).setDropChance(0.0075D).setOffset(0.0F, -0.2F, 0.0F).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ENDER_DRAGON).setDropChance(0.0075D).setRotation(0.0F, 180.0F, 0.0F).setScale(0.25F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.DRAGON_BREATH))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CAMEL).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SNIFFER).setScale(0.5F).setRightClickBehavior(new PullFromLootTableBehavior(BuiltInLootTables.SNIFFER_DIGGING, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.BREEZE).setRightClickBehavior(new ShootProjectileBehavior(new ItemStackTemplate(Items.WIND_CHARGE), 1, false, Optional.of(SoundEvents.BREEZE_SHOOT))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.BOGGED).setRightClickBehavior(new ShootProjectileBehavior(new ItemStackTemplate(Items.TIPPED_ARROW, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON)).build()), 1, true, Optional.of(SoundEvents.ARROW_SHOOT))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ARMADILLO).setScale(1.5F).setRightClickBehavior(new ClickWithItemBehavior(Items.BRUSH, false, new ItemDropBehavior(Items.ARMADILLO_SCUTE), 1000, SoundEvents.ARMADILLO_BRUSH)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CREAKING).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.RESIN_CLUMP, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityTypes.HAPPY_GHAST).setOffset(0.0F, 0.3F, 0.0F).setScale(0.25F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.COPPER_GOLEM)
			.addVariant("weather_state", WeatheringCopper.WeatherState.UNAFFECTED.getSerializedName())
			.addVariant("weather_state", WeatheringCopper.WeatherState.EXPOSED.getSerializedName())
			.addVariant("weather_state", WeatheringCopper.WeatherState.WEATHERED.getSerializedName())
			.addVariant("weather_state", WeatheringCopper.WeatherState.OXIDIZED.getSerializedName())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("weather_state", WeatheringCopper.WeatherState.UNAFFECTED.getSerializedName()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.PARCHED).setRightClickBehavior(new ShootProjectileBehavior(new ItemStackTemplate(Items.TIPPED_ARROW, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WEAKNESS)).build()), 1, true, Optional.of(SoundEvents.ARROW_SHOOT))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.CAMEL_HUSK).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityTypes.NAUTILUS));
		this.makeTrophy(new Trophy.Builder(EntityTypes.ZOMBIE_NAUTILUS)
			.addRegistryVariant("variant", Registries.ZOMBIE_NAUTILUS_VARIANT.identifier())
			.addDefaultVariant(Util.make(new CompoundTag(), tag -> tag.putString("variant", TemperatureVariants.TEMPERATE.toString()))));
		this.makeTrophy(new Trophy.Builder(EntityTypes.SULFUR_CUBE).setScale(1.25F));
	}

	private CompoundTag buildPandaVariant(String gene) {
		CompoundTag tag = new CompoundTag();
		tag.putString("MainGene", gene);
		tag.putString("HiddenGene", gene);
		return tag;
	}

	public int setHorseVariant(Variant variant, Markings markings) {
		return variant.getId() & 255 | markings.getId() << 8 & '\uff00';
	}

	@Override
	public String getName() {
		return "Default OpenBlocks Trophies";
	}
}
