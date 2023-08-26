package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ClickWithItemBehavior(Either<ItemStack, TagKey<Item>> ingredient, boolean consumeStack, Optional<CustomBehavior> behavior, int cooldown, Optional<SoundEvent> sound) implements CustomBehavior {

	public static final Codec<ClickWithItemBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(ItemStack.CODEC, TagKey.codec(Registries.ITEM)).fieldOf("item_to_use").forGetter(ClickWithItemBehavior::ingredient),
			Codec.BOOL.fieldOf("shrink_item_stack").forGetter(ClickWithItemBehavior::consumeStack),
			CustomTrophyBehaviors.CODEC.optionalFieldOf("execute_behavior").forGetter(ClickWithItemBehavior::behavior),
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
		this(Either.left(new ItemStack(clickedItem)), consumeStack, Optional.ofNullable(executeBehavior), cooldown, Optional.ofNullable(sound));
	}

	public ClickWithItemBehavior(TagKey<Item> clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this(Either.right(clickedItem), consumeStack, Optional.ofNullable(executeBehavior), cooldown, Optional.ofNullable(sound));
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.CLICK_WITH_ITEM.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		boolean valid = false;
		if (this.ingredient().left().isPresent() && ItemStack.isSameItem(usedItem, this.ingredient().left().get())) {
			valid = true;
		} else if (this.ingredient().right().isPresent() && usedItem.is(this.ingredient().right().get())) {
			valid = true;
		}
		if (valid) {
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
