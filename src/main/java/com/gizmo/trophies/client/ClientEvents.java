package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.block.TrophyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = OpenBlocksTrophies.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

	@SubscribeEvent
	public static void registerBERenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TrophyRegistries.TROPHY_BE.get(), TrophyRenderer::new);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
//			if (ModList.get().isLoaded("twilightforest")) {
//				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.ARMORED_GIANT.get(), entity -> {
//					ArmoredGiant giant = (ArmoredGiant) entity;
//					giant.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
//					giant.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
//					giant.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
//					giant.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
//				});
//
//				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.KNIGHT_PHANTOM.get(), entity -> {
//					KnightPhantom phantom = (KnightPhantom) entity;
//					phantom.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TFItems.PHANTOM_HELMET.get()));
//					phantom.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TFItems.PHANTOM_CHESTPLATE.get()));
//				});
//
//				TrophyExtraRendering.addExtraRenderForEntity(TFEntities.SNOW_GUARDIAN.get(), entity -> {
//					SnowGuardian guardian = (SnowGuardian) entity;
//					guardian.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TFItems.ARCTIC_HELMET.get()));
//					guardian.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TFItems.ARCTIC_CHESTPLATE.get()));
//				});
//			}
//			if (ModList.get().isLoaded("rats")) {
//				TrophyExtraRendering.addExtraRenderForEntity(RatlantisEntityRegistry.DUTCHRAT.get(), entity -> {
//					Dutchrat pirat = (Dutchrat) entity;
//					pirat.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatlantisItemRegistry.GHOST_PIRAT_HAT.get()));
//				});
//
//				TrophyExtraRendering.addExtraRenderForEntity(RatlantisEntityRegistry.GHOST_PIRAT.get(), entity -> {
//					GhostPirat pirat = (GhostPirat) entity;
//					pirat.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatlantisItemRegistry.GHOST_PIRAT_HAT.get()));
//				});
//
//				TrophyExtraRendering.addExtraRenderForEntity(RatlantisEntityRegistry.PIRAT.get(), entity -> {
//					Pirat pirat = (Pirat) entity;
//					pirat.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatsItemRegistry.PIRAT_HAT.get()));
//				});
//
//				TrophyExtraRendering.addExtraRenderForEntity(RatlantisEntityRegistry.RAT_BARON.get(), entity -> {
//					RatBaron baron = (RatBaron) entity;
//					baron.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatlantisItemRegistry.AVIATOR_HAT.get()));
//				});
//			}
		});
	}

	@Mod.EventBusSubscriber(modid = OpenBlocksTrophies.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeClientEvents {

		//disables rendering the trophy hitbox if there's no visible pedestal.
		//why? because I hate when hitboxes don't fit the block, and making it fit depending on entity is impossible.
		//so, we'll just make the hitbox rather large (almost a full block) but invisible.
		@SubscribeEvent
		public static void dontRenderTrophyHitbox(RenderHighlightEvent.Block event) {
			BlockState state = event.getCamera().getEntity().level().getBlockState(event.getTarget().getBlockPos());
			if (state.is(TrophyRegistries.TROPHY.get()) && !state.getValue(TrophyBlock.PEDESTAL)) {
				event.setCanceled(true);
			}
		}
	}
}
