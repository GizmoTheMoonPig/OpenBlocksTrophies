package com.gizmo.trophies.trophy;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class AmbientSoundFetcher {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final Method Mob_getAmbientSound = ObfuscationReflectionHelper.findMethod(Mob.class, "m_7515_");
	private static final MethodHandle handle_Mob_getAmbientSound;

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = LOOKUP.unreflect(Mob_getAmbientSound);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		handle_Mob_getAmbientSound = tmp_handle_Mob_getAmbientSound;
	}

	@Nullable
	public static SoundEvent getAmbientSound(EntityType<?> type, Level level) {
		SoundEvent sound = null;
		Entity entity = type.create(level);
		if (handle_Mob_getAmbientSound != null && entity instanceof Mob mob) {
			try {
				sound = (SoundEvent) handle_Mob_getAmbientSound.invokeExact(mob);
			} catch (Throwable e) {
				//fail silently, doesnt matter as this method can be null
			}
		}
		return sound;
	}
}
