package com.gizmo.trophies.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a way to do some extra work to an entity before making it actually appear on the trophy.
 * <br>
 * This can be useful in a few ways: <br>
 * - Rendering armor on an entity <br>
 * - Rendering an item in the entity's hand <br>
 * - Configuring the entity's methods, such as setting a profession on a villager or changing fish colors <br>
 * <br>
 * To add an entity to this list, simply call {@link #addExtraRenderForEntity(EntityType, ExtraRender)} in {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}
 */
public class TrophyExtraRendering {

	private static final Map<EntityType<?>, ExtraRender> RENDER_MAP = new HashMap<>();

	public static ExtraRender getRenderForEntity(EntityType<?> type) {
		return RENDER_MAP.get(type);
	}

	public static Map<EntityType<?>, ExtraRender> getRenderMap() {
		return RENDER_MAP;
	}

	/**
	 * Register an entity for extra rendering work here!
	 * @param type the entity type to call this for
	 * @param render the render configuration. This takes in the entity based on the entity type you provide, so It's safe to cast it to its proper entity.
	 */
	public static void addExtraRenderForEntity(EntityType<?> type, ExtraRender render) {
		RENDER_MAP.put(type, render);
	}

	public interface ExtraRender {
		void createExtraRender(Entity entity);
	}
}
