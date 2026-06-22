package com.gizmo.trophies.criteria;

import com.gizmo.trophies.init.TrophyCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TrophyInteractionCriteria extends AbstractTrophyCriteria {

	public static final MapCodec<TrophyInteractionCriteria> CODEC = RecordCodecBuilder.mapCodec(instance ->
		baseCodec(instance).and(instance.group(
				Ingredient.CODEC.fieldOf("item_to_use").forGetter(o -> o.useItem),
				Codec.BOOL.fieldOf("shrink_item").forGetter(o -> o.shrinkUseItem),
				SoundEvent.DIRECT_CODEC.optionalFieldOf("sound").forGetter(o -> o.playSound)))
			.apply(instance, TrophyInteractionCriteria::new));

	private final Ingredient useItem;
	private final boolean shrinkUseItem;
	private final Optional<SoundEvent> playSound;

	public TrophyInteractionCriteria(EntityType<?> entity, Optional<CompoundTag> variant, Ingredient useItem, boolean shrinkUseItem, Optional<SoundEvent> playSound) {
		super(entity, variant);
		this.useItem = useItem;
		this.shrinkUseItem = shrinkUseItem;
		this.playSound = playSound;
	}

	public Ingredient getUseItem() {
		return this.useItem;
	}

	public boolean shouldShrinkUseItem() {
		return this.shrinkUseItem;
	}

	@Nullable
	public SoundEvent interactionSound() {
		return this.playSound.orElse(null);
	}

	@Override
	public TrophyCriteriaType getType() {
		return TrophyCriteria.INTERACTION.get();
	}
}
