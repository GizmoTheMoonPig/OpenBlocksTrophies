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
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record ItemDropBehavior(ItemStackTemplate itemToDrop, int cooldown, Optional<SoundEvent> sound) implements CustomBehavior {

	public static final MapCodec<ItemDropBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemDropBehavior::itemToDrop),
			Codec.INT.optionalFieldOf("cooldown", 0).forGetter(ItemDropBehavior::cooldown),
			SoundEvent.DIRECT_CODEC.optionalFieldOf("sound").forGetter(ItemDropBehavior::sound)
	).apply(instance, ItemDropBehavior::new));

	public ItemDropBehavior(Item itemToDrop) {
		this(new ItemStackTemplate(itemToDrop), 0, Optional.empty());
	}

	public ItemDropBehavior(Item itemToDrop, int cooldown) {
		this(new ItemStackTemplate(itemToDrop), cooldown, Optional.empty());
	}

	public ItemDropBehavior(Item itemToDrop, int cooldown, @Nullable SoundEvent sound) {
		this(new ItemStackTemplate(itemToDrop), cooldown, Optional.ofNullable(sound));
	}

	@Override
	public CustomBehaviorType getType() {
		return TrophyBehaviors.ITEM_DROP.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		if (this.sound().isPresent()) {
			player.level().playSound(null, player.blockPosition(), this.sound().get(), SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
		}
		player.getInventory().placeItemBackInInventory(this.itemToDrop().create().copy());
		return this.cooldown();
	}
}
