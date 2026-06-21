package com.gizmo.trophies.item;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class TrophyItem extends BlockItem {

	public TrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (!context.getItemInHand().has(TrophyComponents.TROPHY_INFO)) {
			return false;
		}
		return super.placeBlock(context, state);
	}

	@Override
	public Component getName(ItemStack stack) {
		TrophyInfo info = stack.getComponents().get(TrophyComponents.TROPHY_INFO);
		if (info != null) {
			Trophy trophy = TrophyHelper.getTrophy(info);
			if (!stack.has(DataComponents.PROFILE) && trophy != null && !TrophyHelper.hasCycleOnTrophy(info)) {
				return Component.translatable(TranslatableStrings.TROPHY_WITH_ENTITY, trophy.type().getDescription().plainCopy().getString());
			}
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		TrophyInfo info = stack.getComponents().get(TrophyComponents.TROPHY_INFO);
		if (info != null) {
			Trophy trophy = TrophyHelper.getTrophy(info);
			if (trophy != null) {
				if (display.shows(TrophyComponents.TROPHY_INFO.get())) {
					tooltip.accept(Component.translatable(TranslatableStrings.FROM_MOD_ID, this.getModIdForTooltip(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophy.type())).getNamespace())).withStyle(ChatFormatting.GRAY));
					if (flag.isAdvanced()) {
						CompoundTag variant = TrophyHelper.getTrophyVariant(info);
						HolderLookup.Provider provider = context.registries();
						if (provider != null && !trophy.getVariants(provider).isEmpty() && !variant.isEmpty()) {
							variant.keySet().forEach(s -> tooltip.accept(Component.translatable(TranslatableStrings.VARIANT_FORMATTER, s, variant.get(s).toString()).withStyle(ChatFormatting.GRAY)));
						}
					}
				}
			} else {
				tooltip.accept(Component.translatable(TranslatableStrings.INVALID_DATA).withStyle(ChatFormatting.RED));
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
