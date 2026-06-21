package com.gizmo.trophies.item;

import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class DisplayTrophyItem extends BlockItem {
	public DisplayTrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (!context.getItemInHand().has(TrophyComponents.DISPLAY_TROPHY_INFO)) {
			return false;
		}
		return super.placeBlock(context, state);
	}

	@Override
	public Component getName(ItemStack stack) {
		DisplayTrophy trophy = TrophyHelper.getDisplayTrophy(stack.getComponents());
		if (trophy != null) {
			return Component.translatable("block.obtrophies.display_trophy.display", Component.translatable(trophy.displayItem().getDescriptionId()));
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
		DisplayTrophy trophy = TrophyHelper.getDisplayTrophy(stack.getComponents());
		if (trophy == null) {
			tooltip.accept(Component.translatable(TranslatableStrings.INVALID_DATA).withStyle(ChatFormatting.RED));
		}
	}

	@Override
	@Nullable
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}
}
