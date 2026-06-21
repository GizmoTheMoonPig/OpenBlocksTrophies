package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AmbientSoundFetcher {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final Method Mob_getAmbientSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getAmbientSound");
	@Nullable
	private static final MethodHandle handle_Mob_getAmbientSound;
	private static final Map<EntityType<?>, @Nullable SoundEvent> SOUND_CACHE = new HashMap<>();

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = LOOKUP.unreflect(Mob_getAmbientSound);
		} catch (IllegalAccessException e) {
			OpenBlocksTrophies.LOGGER.error("Could not unreflect mob ambient sounds: ", e);
		}
		handle_Mob_getAmbientSound = tmp_handle_Mob_getAmbientSound;
	}

	@Nullable
	public static SoundEvent getAmbientSound(EntityType<?> type, Level level) {
		if (!SOUND_CACHE.containsKey(type)) {
			SoundEvent sound = null;
			Entity entity = type.create(level, EntitySpawnReason.LOAD);
			if (handle_Mob_getAmbientSound != null && entity instanceof Mob mob) {
				try {
					sound = (SoundEvent) handle_Mob_getAmbientSound.invokeExact(mob);
				} catch (Throwable e) {
					//fail silently, doesn't matter as this method can be null
				}
			}
			SOUND_CACHE.put(type, sound);
			return sound;
		}
		return SOUND_CACHE.get(type);
	}
}
