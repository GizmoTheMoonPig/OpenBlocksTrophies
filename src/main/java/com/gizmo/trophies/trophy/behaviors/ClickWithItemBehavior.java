package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClickWithItemBehavior extends CustomBehavior {

	private final String clickedItemOrTag;
	private final boolean consumeStack;
	@Nullable
	private final CustomBehavior executeBehavior;
	private final int cooldown;
	@Nullable
	private final SoundEvent sound;

	public ClickWithItemBehavior() {
		this(Items.AIR, true, null, 0, null);
	}

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior) {
		this(clickedItem, consumeStack, executeBehavior, 0, null);
	}

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, @Nullable SoundEvent sound) {
		this(clickedItem, consumeStack, executeBehavior, 0, sound);
	}

	public ClickWithItemBehavior(Item clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(clickedItem)).toString(), consumeStack, executeBehavior, cooldown, sound);
	}

	public ClickWithItemBehavior(TagKey<Item> clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this("#" + Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(clickedItem).getKey().location(), consumeStack, executeBehavior, cooldown, sound);
	}

	private ClickWithItemBehavior(String clickedItem, boolean consumeStack, @Nullable CustomBehavior executeBehavior, int cooldown, @Nullable SoundEvent sound) {
		this.clickedItemOrTag = clickedItem;
		this.consumeStack = consumeStack;
		this.executeBehavior = executeBehavior;
		this.cooldown = cooldown;
		this.sound = sound;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("right_click_item");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("item_to_use", context.serialize(this.clickedItemOrTag));
		object.add("shrink_item_stack", context.serialize(this.consumeStack));
		if (this.executeBehavior != null) {
			object.add("execute_behavior", context.serialize(this.executeBehavior.serializeBehavior(context)));
		}
		if (this.cooldown != 0) {
			object.add("cooldown", context.serialize(this.cooldown));
		}
		if (this.sound != null) {
			object.add("sound", context.serialize(Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getKey(this.sound)).toString()));
		}
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		String jsonItem = GsonHelper.getAsString(object, "item_to_use");
		boolean consume = GsonHelper.getAsBoolean(object, "shrink_item_stack");
		CustomBehavior behavior = null;
		if (object.has("execute_behavior")) {
			try {
				JsonObject bObject = GsonHelper.convertToJsonObject(object.get("execute_behavior"), "execute_behavior");
				CustomBehavior fetchedBehavior = CustomBehaviorRegistry.getBehavior(Objects.requireNonNull(ResourceLocation.tryParse(GsonHelper.getAsString(bObject, "type"))));
				behavior = fetchedBehavior.fromJson(bObject);
			} catch (Exception e) {
				OpenBlocksTrophies.LOGGER.error("Could not fetch execute behavior, setting to null", e);
			}
		}
		int cooldown = GsonHelper.getAsInt(object, "cooldown", 0);
		SoundEvent sound = null;
		if (object.has("sound")) {
			sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(object, "sound")));
		}
		return new ClickWithItemBehavior(jsonItem, consume, behavior, cooldown, sound);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		List<Item> items = new ArrayList<>();
		try {
			if (this.clickedItemOrTag.startsWith("#")) {
				items.addAll(Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(Objects.requireNonNull(ResourceLocation.tryParse(this.clickedItemOrTag.replace("#", ""))))).stream().toList());
			} else {
				items.add(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(this.clickedItemOrTag)));
			}
		} catch (Exception e) {
			OpenBlocksTrophies.LOGGER.error("{} threw an error!", this.getClass().getName(), e);
		}
		if (!items.isEmpty() && items.contains(usedItem.getItem())) {
			if (this.executeBehavior != null) {
				this.executeBehavior.execute(block, player, usedItem);
			}
			if (this.consumeStack && !player.isCreative()) {
				usedItem.shrink(1);
			}
			if (this.sound != null) {
				player.getLevel().playSound(null, player.blockPosition(), this.sound, SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
			}
			return this.cooldown;
		}
		return 0;
	}
}
