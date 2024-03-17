package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.behavior.*;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public class TrophyGenerator extends TrophyProvider {
	public TrophyGenerator(PackOutput output) {
		super(output, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void createTrophies() {
		this.makeTrophy(new Trophy.Builder(EntityType.PLAYER));
		this.makeTrophy(new Trophy.Builder(EntityType.WOLF).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityType.CHICKEN).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.EGG, 10000, SoundEvents.CHICKEN_EGG)));
		this.makeTrophy(new Trophy.Builder(EntityType.COW).setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), SoundEvents.COW_MILK)));
		this.makeTrophy(new Trophy.Builder(EntityType.CREEPER).setRightClickBehavior(new ExplosionBehavior(2, false))
				.addVariant("powered", false)
				.addVariant("powered", true));
		this.makeTrophy(new Trophy.Builder(EntityType.SKELETON).setRightClickBehavior(new ShootArrowBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIFIED_PIGLIN).setRightClickBehavior(new ItemDropBehavior(Items.GOLD_NUGGET, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.BAT).setScale(1.4F));
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE));
		this.makeTrophy(new Trophy.Builder(EntityType.WITCH).setScale(0.9F).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 70, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.VILLAGER).addRegistryVariant("profession", Registries.VILLAGER_PROFESSION.location()));
		this.makeTrophy(new Trophy.Builder(EntityType.OCELOT));
		this.makeTrophy(new Trophy.Builder(EntityType.SHEEP)
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
				.addVariant("Color", DyeColor.BLACK.getId()));
		this.makeTrophy(new Trophy.Builder(EntityType.BLAZE).setRightClickBehavior(new PlayerSetFireBehavior(4)));
		this.makeTrophy(new Trophy.Builder(EntityType.SILVERFISH).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityType.SPIDER));
		this.makeTrophy(new Trophy.Builder(EntityType.CAVE_SPIDER).setRightClickBehavior(new MobEffectBehavior(MobEffects.POISON, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.SLIME).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityType.GHAST).setVerticalOffset(0.35D).setScale(0.25F));
		this.makeTrophy(new Trophy.Builder(EntityType.ENDERMAN).setScale(0.75F).setRightClickBehavior(new ShootEnderPearlBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.MAGMA_CUBE).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityType.SQUID).setVerticalOffset(0.5D).setRightClickBehavior(new PlaceBlockBehavior(Blocks.WATER, PlaceBlockBehavior.PlacementMethod.ABOVE)));
		this.makeTrophy(new Trophy.Builder(EntityType.MOOSHROOM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.RED_MUSHROOM, PlaceBlockBehavior.PlacementMethod.AROUND))
				.addVariant("Type", MushroomCow.MushroomType.RED.getSerializedName())
				.addVariant("Type", MushroomCow.MushroomType.BROWN.getSerializedName()));
		this.makeTrophy(new Trophy.Builder(EntityType.IRON_GOLEM).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityType.SNOW_GOLEM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.SNOW, PlaceBlockBehavior.PlacementMethod.AROUND))
				.addVariant("Pumpkin", true)
				.addVariant("Pumpkin", false));
		this.makeTrophy(new Trophy.Builder(EntityType.PIG).setRightClickBehavior(new ItemDropBehavior(Items.PORKCHOP, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.ENDERMITE).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityType.GUARDIAN).setRightClickBehavior(new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.RABBIT).setScale(2.0F).setRightClickBehavior(new ItemDropBehavior(Items.CARROT, 20000))
				.addVariant("RabbitType", Rabbit.Variant.BROWN.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.WHITE.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.BLACK.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.WHITE_SPLOTCHED.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.GOLD.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.SALT.ordinal())
				.addVariant("RabbitType", Rabbit.Variant.EVIL.ordinal()));
		this.makeTrophy(new Trophy.Builder(EntityType.POLAR_BEAR).setRightClickBehavior(new ItemDropBehavior(Items.COD, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.SHULKER).setRightClickBehavior(new MobEffectBehavior(MobEffects.LEVITATION, 100, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000))
				.addVariant("Variant", 0)
				.addVariant("Variant", 1)
				.addVariant("Variant", 2)
				.addVariant("Variant", 3)
				.addVariant("Variant", 4)
				.addVariant("Variant", 5)
				.addVariant("Variant", 6)
				.addVariant("Variant", 256)
				.addVariant("Variant", 257)
				.addVariant("Variant", 258)
				.addVariant("Variant", 259)
				.addVariant("Variant", 260)
				.addVariant("Variant", 261)
				.addVariant("Variant", 262)
				.addVariant("Variant", 512)
				.addVariant("Variant", 513)
				.addVariant("Variant", 514)
				.addVariant("Variant", 515)
				.addVariant("Variant", 516)
				.addVariant("Variant", 517)
				.addVariant("Variant", 518)
				.addVariant("Variant", 768)
				.addVariant("Variant", 769)
				.addVariant("Variant", 770)
				.addVariant("Variant", 771)
				.addVariant("Variant", 772)
				.addVariant("Variant", 773)
				.addVariant("Variant", 774)
				.addVariant("Variant", 1024)
				.addVariant("Variant", 1025)
				.addVariant("Variant", 1026)
				.addVariant("Variant", 1027)
				.addVariant("Variant", 1028)
				.addVariant("Variant", 1029)
				.addVariant("Variant", 1030));
		this.makeTrophy(new Trophy.Builder(EntityType.SKELETON_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.BONE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.DONKEY).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.MULE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.LLAMA).setScale(0.9F).setRightClickBehavior(new ShootLlamaSpitBehavior())
				.addVariant("Variant", 0)
				.addVariant("Variant", 1)
				.addVariant("Variant", 2)
				.addVariant("Variant", 3));
		this.makeTrophy(new Trophy.Builder(EntityType.ELDER_GUARDIAN).setScale(0.5F).setRightClickBehavior(new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.WITHER_SKELETON).setRightClickBehavior(new ItemDropBehavior(Items.WITHER_SKELETON_SKULL, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityType.STRAY).setRightClickBehavior(new ShootArrowBehavior(1, MobEffects.MOVEMENT_SLOWDOWN)));
		this.makeTrophy(new Trophy.Builder(EntityType.HUSK).setRightClickBehavior(new ItemDropBehavior(Items.FEATHER, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE_VILLAGER).addRegistryVariant("profession", Registries.VILLAGER_PROFESSION.location()));
		this.makeTrophy(new Trophy.Builder(EntityType.EVOKER).setRightClickBehavior(new TotemOfUndyingEffectBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.VEX).setScale(1.5F));
		this.makeTrophy(new Trophy.Builder(EntityType.VINDICATOR).setRightClickBehavior(new ItemDropBehavior(Items.IRON_AXE, 20000)));

		//newbies - 1.12+ mobs
		this.makeTrophy(new Trophy.Builder(EntityType.PARROT).setScale(1.75F)
				.addVariant("Variant", 0)
				.addVariant("Variant", 1)
				.addVariant("Variant", 2)
				.addVariant("Variant", 3)
				.addVariant("Variant", 4));
		this.makeTrophy(new Trophy.Builder(EntityType.ILLUSIONER).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 100, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.COD).setScale(1.75F));
		this.makeTrophy(new Trophy.Builder(EntityType.SALMON).setScale(1.25F));
		this.makeTrophy(new Trophy.Builder(EntityType.TROPICAL_FISH).setScale(2.0F)
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
				.addVariant("Variant", TropicalFish.packVariant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED)));
		this.makeTrophy(new Trophy.Builder(EntityType.PUFFERFISH).setScale(2.0F).setRightClickBehavior(new MobEffectBehavior(MobEffects.CONFUSION, 100, 0))
				.addVariant("PuffState", 0)
				.addVariant("PuffState", 1)
				.addVariant("PuffState", 2));
		this.makeTrophy(new Trophy.Builder(EntityType.DOLPHIN).setRightClickBehavior(new MobEffectBehavior(MobEffects.DOLPHINS_GRACE, 300, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.DROWNED).setRightClickBehavior(new ItemDropBehavior(Items.NAUTILUS_SHELL, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityType.PHANTOM).setRightClickBehavior(new ItemDropBehavior(Items.PHANTOM_MEMBRANE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.TURTLE));
		this.makeTrophy(new Trophy.Builder(EntityType.CAT).setScale(1.25F).setRightClickBehavior(new PullFromLootTableBehavior(BuiltInLootTables.CAT_MORNING_GIFT, 20000)).addRegistryVariant("variant", Registries.CAT_VARIANT.location()));
		this.makeTrophy(new Trophy.Builder(EntityType.FOX).setRightClickBehavior(new ItemDropBehavior(Items.SWEET_BERRIES, 20000))
				.addVariant("Type", Fox.Type.RED.getSerializedName())
				.addVariant("Type", Fox.Type.SNOW.getSerializedName()));
		this.makeTrophy(new Trophy.Builder(EntityType.PANDA).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.BAMBOO, 20000))
				.addVariant(this.buildPandaVariant(Panda.Gene.NORMAL.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.LAZY.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.WORRIED.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.PLAYFUL.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.AGGRESSIVE.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.WEAK.getSerializedName()))
				.addVariant(this.buildPandaVariant(Panda.Gene.BROWN.getSerializedName())));
		this.makeTrophy(new Trophy.Builder(EntityType.PILLAGER).setRightClickBehavior(new ShootArrowBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.RAVAGER).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.SADDLE, 50000)));
		this.makeTrophy(new Trophy.Builder(EntityType.TRADER_LLAMA).setRightClickBehavior(new ShootLlamaSpitBehavior()));
		this.makeTrophy(new Trophy.Builder(EntityType.WANDERING_TRADER).setRightClickBehavior(new ItemDropBehavior(Items.EMERALD, 10000, SoundEvents.WANDERING_TRADER_YES)));
		this.makeTrophy(new Trophy.Builder(EntityType.BEE).setScale(1.5F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.HONEY_BOTTLE, 0), 20000, SoundEvents.BOTTLE_FILL)));
		this.makeTrophy(new Trophy.Builder(EntityType.HOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.LEATHER, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.PIGLIN).setRightClickBehavior(new ClickWithItemBehavior(Tags.Items.INGOTS_GOLD, true, new PullFromLootTableBehavior(BuiltInLootTables.PIGLIN_BARTERING, 0), 200, SoundEvents.PIGLIN_ADMIRING_ITEM)));
		this.makeTrophy(new Trophy.Builder(EntityType.STRIDER));
		this.makeTrophy(new Trophy.Builder(EntityType.ZOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 10000)));
		this.makeTrophy(new Trophy.Builder(EntityType.PIGLIN_BRUTE).setRightClickBehavior(new ItemDropBehavior(Items.GOLDEN_AXE, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.AXOLOTL).setScale(1.25F).addCondition(new NotCondition(new ModLoadedCondition("mavm")))
				.addVariant("Variant", 0)
				.addVariant("Variant", 1)
				.addVariant("Variant", 2)
				.addVariant("Variant", 3)
				.addVariant("Variant", 4));
		this.makeTrophy(new Trophy.Builder(EntityType.GLOW_SQUID).setVerticalOffset(0.5D).setRightClickBehavior(new MobEffectBehavior(MobEffects.GLOWING, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.GOAT).setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), 0, SoundEvents.GOAT_MILK)));
		this.makeTrophy(new Trophy.Builder(EntityType.ALLAY).setScale(1.75F));
		this.makeTrophy(new Trophy.Builder(EntityType.FROG).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.SLIME_BALL, 20000)).addRegistryVariant("variant", Registries.FROG_VARIANT.location()));
		this.makeTrophy(new Trophy.Builder(EntityType.TADPOLE).setScale(2.0F));
		this.makeTrophy(new Trophy.Builder(EntityType.WARDEN).setScale(0.75F).setRightClickBehavior(new MobEffectBehavior(MobEffects.DARKNESS, 200, 0)));
		this.makeTrophy(new Trophy.Builder(EntityType.WITHER).setDropChance(0.0075D).setVerticalOffset(-0.2D).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityType.ENDER_DRAGON).setDropChance(0.0075D).setScale(0.25F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.DRAGON_BREATH))));
		this.makeTrophy(new Trophy.Builder(EntityType.CAMEL).setScale(0.75F));
		this.makeTrophy(new Trophy.Builder(EntityType.SNIFFER).setScale(0.5F).setRightClickBehavior(new PullFromLootTableBehavior(BuiltInLootTables.SNIFFER_DIGGING, 20000)));
		this.makeTrophy(new Trophy.Builder(EntityType.BREEZE));
	}

	private CompoundTag buildPandaVariant(String gene) {
		CompoundTag tag = new CompoundTag();
		tag.putString("MainGene", gene);
		tag.putString("HiddenGene", gene);
		return tag;
	}

	@Override
	public String getName() {
		return "Default OpenBlocks Trophies";
	}
}
