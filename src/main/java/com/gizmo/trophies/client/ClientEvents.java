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

@Mod.EventBusSubscriber(modid = OpenBlocksTrophies.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

	@SubscribeEvent
	public static void registerBERenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TrophyRegistries.TROPHY_BE.get(), TrophyRenderer::new);
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
