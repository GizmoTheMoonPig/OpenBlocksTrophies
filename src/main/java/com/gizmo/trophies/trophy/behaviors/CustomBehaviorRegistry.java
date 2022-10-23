package com.gizmo.trophies.trophy.behaviors;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides an easy way to register a custom behavior when a trophy is right-clicked.
 * To register a new behavior, use {@link #registerBehavior(CustomBehavior)} in {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
 */
public class CustomBehaviorRegistry {

	private static final Map<ResourceLocation, CustomBehavior> TROPHY_BEHAVIORS = new HashMap<>();

	/**
	 * Register a new Right Click behavior!
	 *
	 * @param behavior the behavior class to register. You'll need to make a custom class to do this, just make sure you extend {@link CustomBehavior} and implement all the abstract methods.
	 */
	public static void registerBehavior(CustomBehavior behavior) {
		TROPHY_BEHAVIORS.put(behavior.getType(), behavior);
	}

	public static CustomBehavior getBehavior(ResourceLocation type) {
		return TROPHY_BEHAVIORS.get(type);
	}
}
