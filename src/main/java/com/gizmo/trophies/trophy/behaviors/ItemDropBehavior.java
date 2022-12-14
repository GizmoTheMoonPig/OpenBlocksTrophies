package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ItemDropBehavior extends CustomBehavior {
	private Item itemToDrop;
	private int cooldown;
	private SoundEvent sound;

	public ItemDropBehavior() {
	}

	public ItemDropBehavior(Item drop, int cooldown) {
		this(drop, cooldown, null);
	}

	public ItemDropBehavior(Item drop, int cooldown, @Nullable SoundEvent sound) {
		this.itemToDrop = drop;
		this.cooldown = cooldown;
		this.sound = sound;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("item");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("item", context.serialize(ForgeRegistries.ITEMS.getKey(this.itemToDrop).toString()));
		object.add("cooldown", context.serialize(this.cooldown));
		if (this.sound != null) {
			object.add("sound", context.serialize(ForgeRegistries.SOUND_EVENTS.getKey(this.sound).toString()));
		}
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		Item item = GsonHelper.getAsItem(object, "item");
		int cooldown = GsonHelper.getAsInt(object, "cooldown", 1000);
		SoundEvent sound = null;
		if (object.has("sound")) {
			sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(object, "sound")));
		}
		return new ItemDropBehavior(item, cooldown, sound);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player) {
		if (this.sound != null) {
			player.getLevel().playSound(null, player.blockPosition(), this.sound, SoundSource.BLOCKS, 1.0F, (block.getLevel().getRandom().nextFloat() - block.getLevel().getRandom().nextFloat()) * 0.2F + 1.0F);
		}
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(this.itemToDrop).copy());
		return this.cooldown;
	}
}
