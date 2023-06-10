package com.gizmo.trophies.item;

import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.client.TrophyItemRenderer;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TrophyItem extends BlockItem {

	public static final String ENTITY_TAG = "entity";
	public static final String COOLDOWN_TAG = "cooldown";
	public static final String CYCLING_TAG = "SpecialCycleVariant";
	public static final String VARIANT_TAG = "VariantID";

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

	public static boolean hasCycleOnTrophy(@Nonnull ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if (tag != null && tag.contains(TrophyItem.CYCLING_TAG)) {
				return tag.getBoolean(TrophyItem.CYCLING_TAG);
			}
		}

		return false;
	}

	public static ItemStack loadEntityToTrophy(EntityType<?> type, int variant, boolean cycling) {
		ItemStack stack = new ItemStack(TrophyRegistries.TROPHY_ITEM.get());
		CompoundTag tag = new CompoundTag();
		tag.putString(ENTITY_TAG, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(type)).toString());
		tag.putInt(VARIANT_TAG, variant);
		tag.putBoolean(TrophyItem.CYCLING_TAG, cycling);
		stack.addTagElement("BlockEntityTag", tag);
		return stack;
	}

	public static int getTrophyVariant(@Nonnull ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if (tag != null && tag.contains(TrophyItem.VARIANT_TAG)) {
				return tag.getInt(TrophyItem.VARIANT_TAG);
			}
		}

		return 0;
	}

	@Override
	public Component getName(ItemStack stack) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null && !hasCycleOnTrophy(stack)) {
			return Component.translatable("block.obtrophies.trophy.entity", trophy.getType().getDescription().getString());
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null && !hasCycleOnTrophy(stack)) {
			tooltip.add(Component.translatable("item.obtrophies.trophy.modid", this.getModIdForTooltip(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(trophy.getType())).getNamespace())).withStyle(ChatFormatting.GRAY));
			if (flag.isAdvanced()) {
				int variant = getTrophyVariant(stack);
				if (level != null && !trophy.getVariants(level.registryAccess()).isEmpty() && trophy.getVariants(level.registryAccess()).size() >= variant) {
					Map<String, String> variantDefiners = trophy.getVariants(level.registryAccess()).get(variant);
					variantDefiners.forEach((key, value) -> tooltip.add(Component.translatable("\"%s\": \"%s\"", key, value).withStyle(ChatFormatting.GRAY)));
				}
			}
		}
	}

	private String getModIdForTooltip(String modId) {
		return ModList.get().getModContainerById(modId)
				.map(ModContainer::getModInfo)
				.map(IModInfo::getDisplayName)
				.orElseGet(() -> StringUtils.capitalize(modId));
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
