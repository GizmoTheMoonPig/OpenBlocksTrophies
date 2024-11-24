package com.gizmo.trophies.trophy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record DisplayTrophy(Item displayItem, float scale, Vec3 offset, Vec3 rotation, float rotationSpeed, boolean bob, Optional<SoundEvent> rightClickSound) {

	public static final DisplayTrophy FALLBACK = new DisplayTrophy(Blocks.STONE.asItem(), 1.0F, Vec3.ZERO, Vec3.ZERO, 0.0F, false, Optional.empty());

	public static final Codec<DisplayTrophy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.ITEM.byNameCodec().fieldOf("display").forGetter(DisplayTrophy::displayItem),
		Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(DisplayTrophy::scale),
		Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(DisplayTrophy::offset),
		Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(DisplayTrophy::rotation),
		Codec.FLOAT.optionalFieldOf("rotation_speed", 0.0F).forGetter(DisplayTrophy::rotationSpeed),
		Codec.BOOL.optionalFieldOf("bob", false).forGetter(DisplayTrophy::bob),
		SoundEvent.DIRECT_CODEC.optionalFieldOf("click_sound").forGetter(DisplayTrophy::rightClickSound)
	).apply(instance, DisplayTrophy::new));
}
