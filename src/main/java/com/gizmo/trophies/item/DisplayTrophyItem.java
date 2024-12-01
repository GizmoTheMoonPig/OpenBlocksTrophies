package com.gizmo.trophies.item;

import com.gizmo.trophies.client.TrophyItemRenderer;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class DisplayTrophyItem extends BlockItem {
	public DisplayTrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	public static DisplayTrophy getTrophy(@Nonnull ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if (tag != null && tag.contains("display")) {
				return DisplayTrophy.CODEC.parse(NbtOps.INSTANCE, tag.get("display")).result().orElse(null);
			}
		}

		return null;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		DisplayTrophy trophy = getTrophy(stack);
		if (trophy != null) {
			return trophy.displayItem().getRarity(stack);
		}
		return super.getRarity(stack);
	}

	@Override
	public Component getName(ItemStack stack) {
		DisplayTrophy trophy = getTrophy(stack);
		if (trophy != null) {
			return Component.translatable("block.obtrophies.display_trophy.display", trophy.displayItem().getDescription().plainCopy().getString());
		}
		return super.getName(stack);
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
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new TrophyItemRenderer();
			}
		});
	}
}
