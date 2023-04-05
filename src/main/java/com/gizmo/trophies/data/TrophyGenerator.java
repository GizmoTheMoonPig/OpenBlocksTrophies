package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.behaviors.*;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.Tags;

import java.util.Map;

public class TrophyGenerator extends TrophyProvider {
	public TrophyGenerator(DataGenerator generator) {
		super(generator, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void createTrophies() {
		this.makeTrophy(new Trophy.Builder(EntityType.WOLF).build());
		this.makeTrophy(new Trophy.Builder(EntityType.CHICKEN).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.EGG, 10000, SoundEvents.CHICKEN_EGG)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.COW).setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), SoundEvents.COW_MILK)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.CREEPER).setRightClickBehavior(new ExplosionBehavior(2, false))
				.addVariant("powered", "false")
				.addVariant("powered", "true").build());
		this.makeTrophy(new Trophy.Builder(EntityType.SKELETON).setRightClickBehavior(new ShootArrowBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIFIED_PIGLIN).setRightClickBehavior(new ItemDropBehavior(Items.GOLD_NUGGET, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.BAT).setVerticalOffset(-0.3D).setScale(2.0F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE).build());
		this.makeTrophy(new Trophy.Builder(EntityType.WITCH).setScale(0.9F).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 70, 1)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.VILLAGER).addRegistry("profession", Registry.VILLAGER_PROFESSION_REGISTRY).build());
		this.makeTrophy(new Trophy.Builder(EntityType.OCELOT).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SHEEP)
				.addVariant("Color", String.valueOf(DyeColor.WHITE.getId()))
				.addVariant("Color", String.valueOf(DyeColor.ORANGE.getId()))
				.addVariant("Color", String.valueOf(DyeColor.MAGENTA.getId()))
				.addVariant("Color", String.valueOf(DyeColor.LIGHT_BLUE.getId()))
				.addVariant("Color", String.valueOf(DyeColor.YELLOW.getId()))
				.addVariant("Color", String.valueOf(DyeColor.LIME.getId()))
				.addVariant("Color", String.valueOf(DyeColor.PINK.getId()))
				.addVariant("Color", String.valueOf(DyeColor.GRAY.getId()))
				.addVariant("Color", String.valueOf(DyeColor.LIGHT_GRAY.getId()))
				.addVariant("Color", String.valueOf(DyeColor.CYAN.getId()))
				.addVariant("Color", String.valueOf(DyeColor.PURPLE.getId()))
				.addVariant("Color", String.valueOf(DyeColor.BLUE.getId()))
				.addVariant("Color", String.valueOf(DyeColor.BROWN.getId()))
				.addVariant("Color", String.valueOf(DyeColor.GREEN.getId()))
				.addVariant("Color", String.valueOf(DyeColor.RED.getId()))
				.addVariant("Color", String.valueOf(DyeColor.BLACK.getId())).build());
		this.makeTrophy(new Trophy.Builder(EntityType.BLAZE).setRightClickBehavior(new PlayerSetFireBehavior(4)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SILVERFISH).setScale(1.5F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SPIDER).build());
		this.makeTrophy(new Trophy.Builder(EntityType.CAVE_SPIDER).setRightClickBehavior(new MobEffectBehavior(MobEffects.POISON, 200, 3)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SLIME).setScale(1.25F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.GHAST).setVerticalOffset(0.3D).setScale(0.25F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ENDERMAN).setScale(0.75F).setRightClickBehavior(new ShootEnderPearlBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.MAGMA_CUBE).setScale(1.25F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SQUID).setVerticalOffset(0.5D).setRightClickBehavior(new PlaceBlockBehavior(Blocks.WATER, false)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.MOOSHROOM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.RED_MUSHROOM, true))
				.addVariant("Type", "red")
				.addVariant("Type", "brown").build());
		this.makeTrophy(new Trophy.Builder(EntityType.IRON_GOLEM).setScale(0.75F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SNOW_GOLEM).setRightClickBehavior(new PlaceBlockBehavior(Blocks.SNOW, true))
				.addVariant("Pumpkin", "true")
				.addVariant("Pumpkin", "false").build());
		this.makeTrophy(new Trophy.Builder(EntityType.PIG).setRightClickBehavior(new ItemDropBehavior(Items.PORKCHOP, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ENDERMITE).setScale(1.5F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.GUARDIAN).setRightClickBehavior(new ElderGuardianCurseBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.RABBIT).setScale(2.0F).setRightClickBehavior(new ItemDropBehavior(Items.CARROT, 20000))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_BROWN))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_WHITE))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_BLACK))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_WHITE_SPLOTCHED))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_GOLD))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_SALT))
				.addVariant("RabbitType", String.valueOf(Rabbit.TYPE_EVIL)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.POLAR_BEAR).setRightClickBehavior(new ItemDropBehavior(Items.COD, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SHULKER).setRightClickBehavior(new MobEffectBehavior(MobEffects.LEVITATION, 100, 1)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000))
				.addVariant("Variant", String.valueOf(0))
				.addVariant("Variant", String.valueOf(1))
				.addVariant("Variant", String.valueOf(2))
				.addVariant("Variant", String.valueOf(3))
				.addVariant("Variant", String.valueOf(4))
				.addVariant("Variant", String.valueOf(5))
				.addVariant("Variant", String.valueOf(6))
				.addVariant("Variant", String.valueOf(256))
				.addVariant("Variant", String.valueOf(257))
				.addVariant("Variant", String.valueOf(258))
				.addVariant("Variant", String.valueOf(259))
				.addVariant("Variant", String.valueOf(260))
				.addVariant("Variant", String.valueOf(261))
				.addVariant("Variant", String.valueOf(262))
				.addVariant("Variant", String.valueOf(512))
				.addVariant("Variant", String.valueOf(513))
				.addVariant("Variant", String.valueOf(514))
				.addVariant("Variant", String.valueOf(515))
				.addVariant("Variant", String.valueOf(516))
				.addVariant("Variant", String.valueOf(517))
				.addVariant("Variant", String.valueOf(518))
				.addVariant("Variant", String.valueOf(768))
				.addVariant("Variant", String.valueOf(769))
				.addVariant("Variant", String.valueOf(770))
				.addVariant("Variant", String.valueOf(771))
				.addVariant("Variant", String.valueOf(772))
				.addVariant("Variant", String.valueOf(773))
				.addVariant("Variant", String.valueOf(774))
				.addVariant("Variant", String.valueOf(1024))
				.addVariant("Variant", String.valueOf(1025))
				.addVariant("Variant", String.valueOf(1026))
				.addVariant("Variant", String.valueOf(1027))
				.addVariant("Variant", String.valueOf(1028))
				.addVariant("Variant", String.valueOf(1029))
				.addVariant("Variant", String.valueOf(1030)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SKELETON_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.BONE, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE_HORSE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.DONKEY).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.MULE).setScale(0.9F).setRightClickBehavior(new ItemDropBehavior(Items.WHEAT, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.LLAMA).setScale(0.9F).setRightClickBehavior(new ShootLlamaSpitBehavior())
				.addVariant("Variant", String.valueOf(0))
				.addVariant("Variant", String.valueOf(1))
				.addVariant("Variant", String.valueOf(2))
				.addVariant("Variant", String.valueOf(3)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ELDER_GUARDIAN).setScale(0.5F).setRightClickBehavior(new ElderGuardianCurseBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.WITHER_SKELETON).setRightClickBehavior(new ItemDropBehavior(Items.WITHER_SKELETON_SKULL, 50000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.STRAY).setRightClickBehavior(new ShootArrowBehavior(1, MobEffects.MOVEMENT_SLOWDOWN)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.HUSK).setRightClickBehavior(new ItemDropBehavior(Items.FEATHER, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ZOMBIE_VILLAGER).addRegistry("profession", Registry.VILLAGER_PROFESSION_REGISTRY).build());
		this.makeTrophy(new Trophy.Builder(EntityType.EVOKER).setRightClickBehavior(new TotemOfUndyingEffectBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.VEX).setScale(1.5F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.VINDICATOR).setRightClickBehavior(new ItemDropBehavior(Items.IRON_AXE, 20000)).build());

		//newbies - 1.12+ mobs
		this.makeTrophy(new Trophy.Builder(EntityType.PARROT).setScale(1.75F)
				.addVariant("Variant", String.valueOf(0))
				.addVariant("Variant", String.valueOf(1))
				.addVariant("Variant", String.valueOf(2))
				.addVariant("Variant", String.valueOf(3))
				.addVariant("Variant", String.valueOf(4)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ILLUSIONER).setRightClickBehavior(new MobEffectBehavior(MobEffects.BLINDNESS, 100, 1)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.COD).setScale(1.75F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.SALMON).setScale(1.25F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.TROPICAL_FISH).setScale(2.0F)
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED)))
				.addVariant("Variant", String.valueOf(TropicalFish.calculateVariant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED))).build());
		this.makeTrophy(new Trophy.Builder(EntityType.PUFFERFISH).setScale(2.0F).setRightClickBehavior(new MobEffectBehavior(MobEffects.CONFUSION, 100, 0))
				.addVariant("PuffState", String.valueOf(0))
				.addVariant("PuffState", String.valueOf(1))
				.addVariant("PuffState", String.valueOf(2)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.DOLPHIN).setRightClickBehavior(new MobEffectBehavior(MobEffects.DOLPHINS_GRACE, 300, 0)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.DROWNED).setRightClickBehavior(new ItemDropBehavior(Items.NAUTILUS_SHELL, 50000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.PHANTOM).setRightClickBehavior(new ItemDropBehavior(Items.PHANTOM_MEMBRANE, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.TURTLE).build());
		this.makeTrophy(new Trophy.Builder(EntityType.CAT).setScale(1.25F).setRightClickBehavior(new PullFromLootTableBehavior(BuiltInLootTables.CAT_MORNING_GIFT, 20000))
				.addRegistry("variant", Registry.CAT_VARIANT_REGISTRY).build());
		this.makeTrophy(new Trophy.Builder(EntityType.FOX).setRightClickBehavior(new ItemDropBehavior(Items.SWEET_BERRIES, 20000))
				.addVariant("Type", "red")
				.addVariant("Type", "snow").build());
		this.makeTrophy(new Trophy.Builder(EntityType.PANDA).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.BAMBOO, 20000))
				.addVariant(Map.of("MainGene", Panda.Gene.NORMAL.getName(), "HiddenGene", Panda.Gene.NORMAL.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.AGGRESSIVE.getName(), "HiddenGene", Panda.Gene.AGGRESSIVE.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.LAZY.getName(), "HiddenGene", Panda.Gene.LAZY.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.WORRIED.getName(), "HiddenGene", Panda.Gene.WORRIED.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.PLAYFUL.getName(), "HiddenGene", Panda.Gene.PLAYFUL.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.WEAK.getName(), "HiddenGene", Panda.Gene.WEAK.getName()))
				.addVariant(Map.of("MainGene", Panda.Gene.BROWN.getName(), "HiddenGene", Panda.Gene.BROWN.getName())).build());
		this.makeTrophy(new Trophy.Builder(EntityType.PILLAGER).setRightClickBehavior(new ShootArrowBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.RAVAGER).setScale(0.75F).setRightClickBehavior(new ItemDropBehavior(Items.SADDLE, 50000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.TRADER_LLAMA).setRightClickBehavior(new ShootLlamaSpitBehavior()).build());
		this.makeTrophy(new Trophy.Builder(EntityType.WANDERING_TRADER).setRightClickBehavior(new ItemDropBehavior(Items.EMERALD, 10000, SoundEvents.WANDERING_TRADER_YES)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.BEE).setScale(1.5F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.HONEY_BOTTLE, 0), 20000, SoundEvents.BOTTLE_FILL)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.HOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.LEATHER, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.PIGLIN).setRightClickBehavior(new ClickWithItemBehavior(Tags.Items.INGOTS_GOLD, true, new PullFromLootTableBehavior(BuiltInLootTables.PIGLIN_BARTERING, 0), 200, SoundEvents.PIGLIN_ADMIRING_ITEM)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.STRIDER).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ZOGLIN).setScale(0.85F).setRightClickBehavior(new ItemDropBehavior(Items.ROTTEN_FLESH, 10000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.PIGLIN_BRUTE).setRightClickBehavior(new ItemDropBehavior(Items.GOLDEN_AXE, 20000)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.AXOLOTL).setScale(1.25F)
				.addVariant("Variant", "0")
				.addVariant("Variant", "1")
				.addVariant("Variant", "2")
				.addVariant("Variant", "3")
				.addVariant("Variant", "4").build());
		this.makeTrophy(new Trophy.Builder(EntityType.GLOW_SQUID).setVerticalOffset(0.5D).setRightClickBehavior(new MobEffectBehavior(MobEffects.GLOWING, 200, 0)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.GOAT).setRightClickBehavior(new ClickWithItemBehavior(Items.BUCKET, true, new ItemDropBehavior(Items.MILK_BUCKET), 0, SoundEvents.GOAT_MILK)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ALLAY).setScale(1.75F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.FROG).setScale(1.5F).setRightClickBehavior(new ItemDropBehavior(Items.SLIME_BALL, 20000))
				.addRegistry("variant", Registry.FROG_VARIANT_REGISTRY).build());
		this.makeTrophy(new Trophy.Builder(EntityType.TADPOLE).setScale(2.0F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.WARDEN).setScale(0.75F).setRightClickBehavior(new MobEffectBehavior(MobEffects.DARKNESS, 200, 0)).build());
		this.makeTrophy(new Trophy.Builder(EntityType.WITHER).setDropChance(0.0075D).setVerticalOffset(-0.2D).setScale(0.75F).build());
		this.makeTrophy(new Trophy.Builder(EntityType.ENDER_DRAGON).setDropChance(0.0075D).setScale(0.25F).setRightClickBehavior(new ClickWithItemBehavior(Items.GLASS_BOTTLE, true, new ItemDropBehavior(Items.DRAGON_BREATH))).build());
	}

	@Override
	public String getName() {
		return "Default OpenBlocks Trophies";
	}
}
