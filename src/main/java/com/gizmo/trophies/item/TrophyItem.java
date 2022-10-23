package com.gizmo.trophies.item;

import com.gizmo.trophies.client.TrophyItemRenderer;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class TrophyItem extends BlockItem {

	public static final String ENTITY_TAG = "entity";
	public static final String COOLDOWN_TAG = "cooldown";

	public TrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	public static Trophy getTrophy(@Nonnull ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if (tag != null && tag.contains(ENTITY_TAG)) {
				String entityKey = tag.getString(ENTITY_TAG);
				if (Trophy.getTrophies().containsKey(ResourceLocation.tryParse(entityKey))) {
					return Trophy.getTrophies().get(ResourceLocation.tryParse(entityKey));
				}
			}
		}

		return null;
	}

	@Override
	public Component getName(ItemStack stack) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null) {
			return Component.translatable("block.obtrophies.trophy.entity", trophy.type().getDescription().getString());
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null) {
			String untranslated = "item.obtrophies.trophy.modid." + Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type())).getNamespace();
			MutableComponent attempted = Component.translatable(untranslated);

			if (untranslated.equals(attempted.getString())) {
				//dont have a lang entry for a modid? Lets try to format it ourselves!
				//remove underscores and capitalize each first letter
				tooltip.add(Component.translatable("item.obtrophies.trophy.modid", WordUtils.capitalize(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophy.type())).getNamespace().replace('_', ' '))).withStyle(ChatFormatting.GRAY));
			} else {
				//otherwise, use the lang version
				tooltip.add(Component.translatable("item.obtrophies.trophy.modid", attempted.getString()).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
		return slot == EquipmentSlot.HEAD;
	}

	@Override
	@Nullable
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
		if (this.allowedIn(tab)) {
			if (!Trophy.getTrophies().isEmpty()) {
				Map<ResourceLocation, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
				sortedTrophies.putAll(Trophy.getTrophies());
				for (Map.Entry<ResourceLocation, Trophy> trophyEntry : sortedTrophies.entrySet()) {
					ItemStack stack = new ItemStack(this);
					CompoundTag tag = new CompoundTag();
					tag.putString(ENTITY_TAG, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophyEntry.getValue().type())).toString());
					stack.addTagElement("BlockEntityTag", tag);
					stacks.add(stack);
				}
			}
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new TrophyItemRenderer();
			}
		});
	}
}
