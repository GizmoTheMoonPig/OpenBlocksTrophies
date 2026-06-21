package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.init.TrophyBehaviors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record ClickWithItemBehavior(Ingredient ingredient, boolean consumeStack, Optional<CustomBehavior> behavior, int cooldown, Optional<SoundEvent> sound) implements CustomBehavior {

	public static final MapCodec<ClickWithItemBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC.fieldOf("item_to_use").forGetter(ClickWithItemBehavior::ingredient),
			Codec.BOOL.fieldOf("shrink_item_stack").forGetter(ClickWithItemBehavior::consumeStack),
			CustomBehaviorType.DISPATCH_CODEC.optionalFieldOf("execute_behavior").forGetter(ClickWithItemBehavior::behavior),
			Codec.INT.optionalFieldOf("cooldown", 0).forGetter(ClickWithItemBehavior::cooldown),
			SoundEvent.DIRECT_CODEC.optionalFieldOf("sound").forGetter(ClickWithItemBehavior::sound)
	).apply(instance, ClickWithItemBehavior::new));

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior) {
		this(clickedItem, consumeStack, executeBehavior, 0, null);
	}

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, @Nullable SoundEvent sound) {
		this(clickedItem, consumeStack, executeBehavior, 0, sound);
	}

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this(Ingredient.of(clickedItem), consumeStack, Optional.ofNullable(executeBehavior), cooldown, Optional.ofNullable(sound));
	}

	public ClickWithItemBehavior(Ingredient clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this(clickedItem, consumeStack, Optional.ofNullable(executeBehavior), cooldown, Optional.ofNullable(sound));
	}

	@Override
	public CustomBehaviorType getType() {
		return TrophyBehaviors.CLICK_WITH_ITEM.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		if (this.ingredient().test(usedItem)) {
			if (this.behavior().isPresent()) {
				this.behavior().get().execute(block, player, usedItem);
			}
			if (this.consumeStack() && !player.isCreative()) {
				usedItem.shrink(1);
			}
			if (this.sound().isPresent()) {
				player.level().playSound(null, player.blockPosition(), this.sound().get(), SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
			}
			return this.cooldown();
		}
		return 0;
	}
}
