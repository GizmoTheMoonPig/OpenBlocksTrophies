package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.behaviors.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class TrophyGenerator extends TrophyProvider {
	public TrophyGenerator(DataGenerator generator) {
		super(generator, OpenBlocksTrophies.MODID);
	}

	@Override
	protected void createTrophies() {
		this.makeTrophy(new Trophy(EntityType.WOLF));
		this.makeTrophy(new Trophy(EntityType.CHICKEN, 0.0D, 1.5F, new ItemDropBehavior(Items.EGG, 10000, SoundEvents.CHICKEN_EGG)));
		this.makeTrophy(new Trophy(EntityType.COW, new ItemDropBehavior(Items.LEATHER, 20000)));
		this.makeTrophy(new Trophy(EntityType.CREEPER, new ExplosionBehavior(2, false)));
		this.makeTrophy(new Trophy(EntityType.SKELETON, new ShootArrowBehavior()));
		this.makeTrophy(new Trophy(EntityType.ZOMBIFIED_PIGLIN, new ItemDropBehavior(Items.GOLD_NUGGET, 20000)));
		this.makeTrophy(new Trophy(EntityType.BAT, -0.3D, 2.0F));
		this.makeTrophy(new Trophy(EntityType.ZOMBIE));
		this.makeTrophy(new Trophy(EntityType.WITCH, 0.0D, 0.9F, new MobEffectBehavior(MobEffects.BLINDNESS, 70, 1)));
		this.makeTrophy(new Trophy(EntityType.VILLAGER));
		this.makeTrophy(new Trophy(EntityType.OCELOT));
		this.makeTrophy(new Trophy(EntityType.SHEEP));
		this.makeTrophy(new Trophy(EntityType.BLAZE, new PlayerSetFireBehavior(4)));
		this.makeTrophy(new Trophy(EntityType.SILVERFISH, 0.0D, 1.5F));
		this.makeTrophy(new Trophy(EntityType.SPIDER));
		this.makeTrophy(new Trophy(EntityType.CAVE_SPIDER, new MobEffectBehavior(MobEffects.POISON, 200, 3)));
		this.makeTrophy(new Trophy(EntityType.SLIME, 0.0D, 1.25F));
		this.makeTrophy(new Trophy(EntityType.GHAST, 0.3D, 0.25F));
		this.makeTrophy(new Trophy(EntityType.ENDERMAN, 0.0D, 0.75F, new ShootEnderPearlBehavior()));
		this.makeTrophy(new Trophy(EntityType.MAGMA_CUBE, 0.0D, 1.25F));
		this.makeTrophy(new Trophy(EntityType.SQUID, 0.5D, 1.0F, new PlaceBlockBehavior(Blocks.WATER, false)));
		this.makeTrophy(new Trophy(EntityType.MOOSHROOM, new PlaceBlockBehavior(Blocks.RED_MUSHROOM, true)));
		this.makeTrophy(new Trophy(EntityType.IRON_GOLEM, 0.0D, 0.75F));
		this.makeTrophy(new Trophy(EntityType.SNOW_GOLEM, new PlaceBlockBehavior(Blocks.SNOW, true)));
		this.makeTrophy(new Trophy(EntityType.PIG, new ItemDropBehavior(Items.PORKCHOP, 20000)));
		this.makeTrophy(new Trophy(EntityType.ENDERMITE, 0.0D, 1.5F));
		this.makeTrophy(new Trophy(EntityType.GUARDIAN, new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy(EntityType.RABBIT, 0.0D, 2.0F, new ItemDropBehavior(Items.CARROT, 20000)));
		this.makeTrophy(new Trophy(EntityType.POLAR_BEAR, new ItemDropBehavior(Items.COD, 20000)));
		this.makeTrophy(new Trophy(EntityType.SHULKER, new MobEffectBehavior(MobEffects.LEVITATION, 100, 1)));
		this.makeTrophy(new Trophy(EntityType.HORSE, 0.0D, 0.9F, new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy(EntityType.SKELETON_HORSE, 0.0D, 0.9F, new ItemDropBehavior(Items.BONE, 20000)));
		this.makeTrophy(new Trophy(EntityType.ZOMBIE_HORSE, 0.0D, 0.9F, new ItemDropBehavior(Items.ROTTEN_FLESH, 20000)));
		this.makeTrophy(new Trophy(EntityType.DONKEY, 0.0D, 0.9F, new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy(EntityType.MULE, 0.0D, 0.9F, new ItemDropBehavior(Items.WHEAT, 20000)));
		this.makeTrophy(new Trophy(EntityType.LLAMA, 0.0D, 0.9F, new ShootLlamaSpitBehavior()));
		this.makeTrophy(new Trophy(EntityType.ELDER_GUARDIAN, 0.0D, 0.5F, new ElderGuardianCurseBehavior()));
		this.makeTrophy(new Trophy(EntityType.WITHER_SKELETON, new ItemDropBehavior(Items.WITHER_SKELETON_SKULL, 50000)));
		this.makeTrophy(new Trophy(EntityType.STRAY, new ShootArrowBehavior(1, MobEffects.MOVEMENT_SLOWDOWN)));
		this.makeTrophy(new Trophy(EntityType.HUSK, new ItemDropBehavior(Items.FEATHER, 20000)));
		this.makeTrophy(new Trophy(EntityType.ZOMBIE_VILLAGER));
		this.makeTrophy(new Trophy(EntityType.EVOKER, new TotemOfUndyingEffectBehavior()));
		this.makeTrophy(new Trophy(EntityType.VEX, 0.0D, 1.5F));
		this.makeTrophy(new Trophy(EntityType.VINDICATOR, new ItemDropBehavior(Items.IRON_AXE, 20000)));

		//newbies - 1.12+ mobs
		this.makeTrophy(new Trophy(EntityType.PARROT, 0.0D, 1.75F));
		this.makeTrophy(new Trophy(EntityType.ILLUSIONER, new MobEffectBehavior(MobEffects.BLINDNESS, 100, 1)));
		this.makeTrophy(new Trophy(EntityType.COD, 0.0D, 1.75F));
		this.makeTrophy(new Trophy(EntityType.SALMON, 0.0D, 1.25F));
		this.makeTrophy(new Trophy(EntityType.TROPICAL_FISH, 0.0D, 2.0F));
		this.makeTrophy(new Trophy(EntityType.PUFFERFISH, 0.0D, 2.0F, new MobEffectBehavior(MobEffects.CONFUSION, 100, 0)));
		this.makeTrophy(new Trophy(EntityType.DOLPHIN, new MobEffectBehavior(MobEffects.DOLPHINS_GRACE, 300, 0)));
		this.makeTrophy(new Trophy(EntityType.DROWNED, new ItemDropBehavior(Items.NAUTILUS_SHELL, 50000)));
		this.makeTrophy(new Trophy(EntityType.PHANTOM, new ItemDropBehavior(Items.PHANTOM_MEMBRANE, 20000)));
		this.makeTrophy(new Trophy(EntityType.TURTLE));
		this.makeTrophy(new Trophy(EntityType.CAT, 0.0D, 1.25F));
		this.makeTrophy(new Trophy(EntityType.FOX, new ItemDropBehavior(Items.SWEET_BERRIES, 20000)));
		this.makeTrophy(new Trophy(EntityType.PANDA, 0.0D, 0.75F, new ItemDropBehavior(Items.BAMBOO, 20000)));
		this.makeTrophy(new Trophy(EntityType.PILLAGER, new ShootArrowBehavior()));
		this.makeTrophy(new Trophy(EntityType.RAVAGER, 0.0D, 0.75F, new ItemDropBehavior(Items.SADDLE, 50000)));
		this.makeTrophy(new Trophy(EntityType.TRADER_LLAMA, new ShootLlamaSpitBehavior()));
		this.makeTrophy(new Trophy(EntityType.WANDERING_TRADER, new ItemDropBehavior(Items.EMERALD, 10000, SoundEvents.WANDERING_TRADER_YES)));
		this.makeTrophy(new Trophy(EntityType.BEE, 0.0D, 1.5F));
		this.makeTrophy(new Trophy(EntityType.HOGLIN, 0.0D, 0.85F, new ItemDropBehavior(Items.LEATHER, 20000)));
		this.makeTrophy(new Trophy(EntityType.PIGLIN, new ItemDropBehavior(Items.GOLD_INGOT, 35000)));
		this.makeTrophy(new Trophy(EntityType.STRIDER));
		this.makeTrophy(new Trophy(EntityType.ZOGLIN, 0.0D, 0.85F, new ItemDropBehavior(Items.ROTTEN_FLESH, 10000)));
		this.makeTrophy(new Trophy(EntityType.PIGLIN_BRUTE, new ItemDropBehavior(Items.GOLDEN_AXE, 20000)));
		this.makeTrophy(new Trophy(EntityType.AXOLOTL, 0.0D, 1.25F));
		this.makeTrophy(new Trophy(EntityType.GLOW_SQUID, 0.5D, 1.0F, new MobEffectBehavior(MobEffects.GLOWING, 200, 0)));
		this.makeTrophy(new Trophy(EntityType.GOAT));
		this.makeTrophy(new Trophy(EntityType.ALLAY, 0.0D, 1.75F));
		this.makeTrophy(new Trophy(EntityType.FROG, 0.0D, 1.5F, new ItemDropBehavior(Items.SLIME_BALL, 20000)));
		this.makeTrophy(new Trophy(EntityType.TADPOLE, 0.0D, 2.0F));
		this.makeTrophy(new Trophy(EntityType.WARDEN, 0.0D, 0.75F, new MobEffectBehavior(MobEffects.DARKNESS, 200, 0)));
		this.makeTrophy(new Trophy(EntityType.WITHER, 0.075D, -0.2D, 0.75F, null));
		this.makeTrophy(new Trophy(EntityType.ENDER_DRAGON, 0.075D, 0.0D, 0.25F, null));
	}

	@Override
	public String getName() {
		return "Default OpenBlocks Trophies";
	}
}
