package com.gizmo.trophies.item;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TrophyItem extends BlockItem {

	public TrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	public static Trophy getTrophy(@Nullable DataComponentMap map) {
		if (map != null && map.has(TrophyRegistries.TROPHY_INFO.get())) {
			ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(map.get(TrophyRegistries.TROPHY_INFO.get()).type());
			if (Trophy.getTrophies().containsKey(entityKey)) {
				return Trophy.getTrophies().get(entityKey);
			}
		}

		return null;
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (!context.getItemInHand().has(TrophyRegistries.TROPHY_INFO)) {
			return false;
		}
		return super.placeBlock(context, state);
	}

	public static boolean hasCycleOnTrophy(@Nullable DataComponentMap map) {
		if (map != null && map.has(TrophyRegistries.TROPHY_INFO.get())) {
			return map.get(TrophyRegistries.TROPHY_INFO.get()).cycling().isPresent();
		}

		return false;
	}

	public static ItemStack loadEntityToTrophy(EntityType<?> type) {
		return loadVariantToTrophy(type, new CompoundTag());
	}

	public static ItemStack loadVariantToTrophy(EntityType<?> type, CompoundTag variant) {
		ItemStack stack = new ItemStack(TrophyRegistries.TROPHY_ITEM.get());
		stack.set(TrophyRegistries.TROPHY_INFO, new TrophyInfo(type, variant));
		stack.set(DataComponents.RARITY, getTrophyRarity(stack));
		return stack;
	}

	public static ItemStack createCyclingTrophy(EntityType<?> type) {
		ItemStack stack = new ItemStack(TrophyRegistries.TROPHY_ITEM.get());
		stack.set(TrophyRegistries.TROPHY_INFO, new TrophyInfo(type, !Trophy.getTrophies().isEmpty()));
		stack.set(DataComponents.RARITY, getTrophyRarity(stack));
		return stack;
	}

	public static CompoundTag getTrophyVariant(@Nullable DataComponentMap map) {
		if (map != null && map.has(TrophyRegistries.TROPHY_INFO.get())) {
			TrophyInfo info = map.get(TrophyRegistries.TROPHY_INFO.get());
			if (info.variant().isPresent()) {
				return info.variant().get();
			}
		}

		return new CompoundTag();
	}

	public static Rarity getTrophyRarity(ItemStack stack) {
		Trophy trophy = getTrophy(stack.getComponents());
		if (trophy != null) {
			if (trophy.type() == EntityType.PLAYER) {
				return Rarity.EPIC;
			} else if (trophy.type().is(Tags.EntityTypes.BOSSES) || trophy.dropChance() >= Trophy.BOSS_DROP_CHANCE) {
				return Rarity.RARE;
			} else {
				return Rarity.UNCOMMON;
			}
		}
		return Rarity.COMMON;
	}

	@Override
	public Component getName(ItemStack stack) {
		Trophy trophy = getTrophy(stack.getComponents());
		if (trophy != null && !hasCycleOnTrophy(stack.getComponents())) {
			return Component.translatable(TranslatableStrings.TROPHY_WITH_ENTITY, trophy.type().getDescription().plainCopy().getString());
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
		Trophy trophy = getTrophy(stack.getComponents());
		if (trophy != null) {
			if (!hasCycleOnTrophy(stack.getComponents())) {
				tooltip.accept(Component.translatable(TranslatableStrings.FROM_MOD_ID, this.getModIdForTooltip(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophy.type())).getNamespace())).withStyle(ChatFormatting.GRAY));
				if (flag.isAdvanced()) {
					CompoundTag variant = getTrophyVariant(stack.getComponents());
					HolderLookup.Provider provider = context.registries();
					if (provider != null && !trophy.getVariants(provider).isEmpty() && !variant.isEmpty()) {
						variant.keySet().forEach(s -> tooltip.accept(Component.translatable(TranslatableStrings.VARIANT_FORMATTER, s, Objects.requireNonNull(variant.get(s)).asString().orElse("")).withStyle(ChatFormatting.GRAY)));
					}
				}
			}
		} else {
			tooltip.accept(Component.translatable(TranslatableStrings.INVALID_DATA).withStyle(ChatFormatting.RED));
		}
	}

	private String getModIdForTooltip(String modId) {
		return ModList.get().getModContainerById(modId)
				.map(ModContainer::getModInfo)
				.map(IModInfo::getDisplayName)
				.orElseGet(() -> StringUtils.capitalize(modId));
	}

	@Override
	@Nullable
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}
}
