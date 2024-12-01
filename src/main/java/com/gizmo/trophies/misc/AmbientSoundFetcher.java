package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AmbientSoundFetcher {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final Method Mob_getAmbientSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getAmbientSound");
	private static final MethodHandle handle_Mob_getAmbientSound;
	private static final Map<EntityType<?>, Pair<SoundEvent, Float>> SOUND_CACHE = new HashMap<>();

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = LOOKUP.unreflect(Mob_getAmbientSound);
		} catch (IllegalAccessException e) {
			OpenBlocksTrophies.LOGGER.error("Could not unreflect mob ambient sounds: ", e);
		}
		handle_Mob_getAmbientSound = tmp_handle_Mob_getAmbientSound;
	}

	//fetches both the ambient sound and voice pitch to avoid creating multiple entity instances per sound call
	public static Pair<SoundEvent, Float> getAmbientSoundAndPitch(EntityType<?> type, Level level) {
		if (!SOUND_CACHE.containsKey(type)) {
			SoundEvent sound = null;
			float pitch = 1.0F;
			Entity entity = type.create(level);
			if (handle_Mob_getAmbientSound != null && entity instanceof Mob mob) {
				try {
					sound = (SoundEvent) handle_Mob_getAmbientSound.invokeExact(mob);
				} catch (Throwable e) {
					//fail silently, doesn't matter as this method can be null
				}
			}
			if (entity instanceof LivingEntity living) {
				pitch = living.getVoicePitch();
			}
			var pair = Pair.of(sound, pitch);
			SOUND_CACHE.put(type, pair);
			return pair;
		}
		return SOUND_CACHE.get(type);
	}
}
