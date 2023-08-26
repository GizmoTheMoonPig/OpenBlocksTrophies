package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ItemDropBehavior(ItemStack itemToDrop, int cooldown, Optional<SoundEvent> sound) implements CustomBehavior {

	public static final Codec<ItemDropBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.CODEC.fieldOf("item").forGetter(ItemDropBehavior::itemToDrop),
			Codec.INT.optionalFieldOf("cooldown", 0).forGetter(ItemDropBehavior::cooldown),
			SoundEvent.DIRECT_CODEC.optionalFieldOf("sound").forGetter(ItemDropBehavior::sound)
	).apply(instance, ItemDropBehavior::new));

	public ItemDropBehavior(Item itemToDrop) {
		this(new ItemStack(itemToDrop), 0, Optional.empty());
	}

	public ItemDropBehavior(Item itemToDrop, int cooldown) {
		this(new ItemStack(itemToDrop), cooldown, Optional.empty());
	}

	public ItemDropBehavior(Item itemToDrop, int cooldown, @Nullable SoundEvent sound) {
		this(new ItemStack(itemToDrop), cooldown, Optional.ofNullable(sound));
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.ITEM_DROP.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		if (this.sound().isPresent()) {
			player.level().playSound(null, player.blockPosition(), this.sound().get(), SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
		}
		ItemHandlerHelper.giveItemToPlayer(player, this.itemToDrop().copy());
		return this.cooldown();
	}
}
