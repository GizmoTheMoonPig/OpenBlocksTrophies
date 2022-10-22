package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import twilightforest.entity.boss.KnightPhantom;
import twilightforest.entity.monster.ArmoredGiant;
import twilightforest.entity.monster.SnowGuardian;
import twilightforest.init.TFEntities;
import twilightforest.init.TFItems;

@Mod.EventBusSubscriber(modid = OpenBlocksTrophies.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

	@SubscribeEvent
	public static void registerBERenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(Registries.TROPHY_BE.get(), TrophyRenderer::new);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			TrophyExtraRendering.addExtraRenderForEntity(EntityType.ZOMBIE_VILLAGER, entity -> {
				ZombieVillager villager = (ZombieVillager) entity;
				villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NONE));
			});
			TrophyExtraRendering.addExtraRenderForEntity(EntityType.TROPICAL_FISH, entity -> {
				TropicalFish fish = (TropicalFish) entity;
				fish.setVariant(calculateVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE));
			});

			if (ModList.get().isLoaded("twilightforest")) {
				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.ARMORED_GIANT.get(), entity -> {
					ArmoredGiant giant = (ArmoredGiant) entity;
					giant.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
					giant.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
					giant.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
					giant.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
				});

				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.KNIGHT_PHANTOM.get(), entity -> {
					KnightPhantom phantom = (KnightPhantom) entity;
					phantom.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TFItems.PHANTOM_HELMET.get()));
					phantom.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TFItems.PHANTOM_CHESTPLATE.get()));
				});

				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.SNOW_GUARDIAN.get(), entity -> {
					SnowGuardian guardian = (SnowGuardian) entity;
					guardian.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TFItems.ARCTIC_HELMET.get()));
					guardian.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TFItems.ARCTIC_CHESTPLATE.get()));
				});
			}
		});
	}

	private static int calculateVariant(TropicalFish.Pattern pattern, DyeColor base, DyeColor accent) {
		return pattern.getBase() & 255 | (pattern.getIndex() & 255) << 8 | (base.getId() & 255) << 16 | (accent.getId() & 255) << 24;
	}
}
