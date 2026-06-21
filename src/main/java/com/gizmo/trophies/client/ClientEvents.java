package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.client.renderer.TrophyOutlineRenderer;
import com.gizmo.trophies.client.renderer.block.DisplayTrophyRenderer;
import com.gizmo.trophies.client.renderer.block.TrophyRenderer;
import com.gizmo.trophies.client.renderer.item.DisplayTrophySpecialRenderer;
import com.gizmo.trophies.client.renderer.item.TrophySpecialRenderer;
import com.gizmo.trophies.init.TrophyBlockEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents {

	public static final ModelLayerLocation PLAYER_TROPHY = new ModelLayerLocation(OpenBlocksTrophies.prefix("player_trophy"), "main");
	public static final ModelLayerLocation SLIM_PLAYER_TROPHY = new ModelLayerLocation(OpenBlocksTrophies.prefix("slim_player_trophy"), "main");

	public static void init(IEventBus bus) {
		bus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
			event.registerBlockEntityRenderer(TrophyBlockEntities.TROPHY.get(), TrophyRenderer::new);
			event.registerBlockEntityRenderer(TrophyBlockEntities.DISPLAY_TROPHY.get(), DisplayTrophyRenderer::new);
		});
		bus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			event.registerLayerDefinition(PLAYER_TROPHY, () -> LayerDefinition.create(PlayerTrophyModel.createMesh(false), 64, 64));
			event.registerLayerDefinition(SLIM_PLAYER_TROPHY, () -> LayerDefinition.create(PlayerTrophyModel.createMesh(true), 64, 64));
		});
		bus.addListener(RegisterSpecialModelRendererEvent.class, event -> {
			event.register(OpenBlocksTrophies.prefix("trophy"), TrophySpecialRenderer.Unbaked.MAP_CODEC);
			event.register(OpenBlocksTrophies.prefix("display_trophy"), DisplayTrophySpecialRenderer.Unbaked.MAP_CODEC);
		});
		NeoForge.EVENT_BUS.addListener(ClientEvents::registerOutlineRenderer);
	}

	private static void registerOutlineRenderer(ExtractBlockOutlineRenderStateEvent event) {
		if (event.getLevel().getBlockEntity(event.getBlockPos()) instanceof TrophyBlockEntity entity) {
			event.addCustomRenderer(new TrophyOutlineRenderer(event.getBlockState().getValue(TrophyBlock.PEDESTAL) && entity.getPlayerProfile() == null));
		}
	}
}
