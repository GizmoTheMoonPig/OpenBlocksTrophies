package com.gizmo.trophies.compat.emi;

import com.gizmo.trophies.compat.TrophyRecipeViewerConstants;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.trophy.Trophy;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record EmiTrophyRecipe(ResourceLocation id, Trophy trophy, CompoundTag variant) implements EmiRecipe {

	public static final EmiTexture BACKGROUND = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 0, 0, TrophyRecipeViewerConstants.WIDTH, TrophyRecipeViewerConstants.HEIGHT);
	public static final EmiTexture ANY_SOURCE_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 32, 23, 15);
	public static final EmiTexture FAKE_PLAYER_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 0, 16, 16);
	public static final EmiTexture PLAYER_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 16, 16, 16);

	@Override
	public EmiRecipeCategory getCategory() {
		return EmiCompat.TROPHY;
	}

	@Override
	public ResourceLocation getId() {
		return this.id();
	}

	@Override
	public List<EmiIngredient> getInputs() {
		SpawnEggItem egg = DeferredSpawnEggItem.byId(this.trophy().type());
		if (egg != null) {
			return List.of(EmiStack.of(egg));
		}
		return List.of();
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(TrophyItem.loadVariantToTrophy(this.trophy().type(), this.variant())));
	}

	@Override
	public int getDisplayWidth() {
		return TrophyRecipeViewerConstants.WIDTH;
	}

	@Override
	public int getDisplayHeight() {
		return TrophyRecipeViewerConstants.HEIGHT;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0);
		widgets.add(new EmiEntityWidget(this.trophy().type(), 10, 11, 32, this.variant(), this.trophy().defaultData()));


		if (TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.ALL) {
			widgets.addTexture(this.getKillIcon(), 54, 19);
			List<Component> components = new ArrayList<>();
			components.add(Component.translatable(TranslatableStrings.TROPHY_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			if (TrophyConfig.trophyDropSource == TrophyConfig.TrophySourceDrop.FAKE_PLAYER) {
				components.add(Component.translatable(TranslatableStrings.TROPHY_FAKE_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			}
			widgets.addTooltipText(components, 54, 19, 16, 16);
		} else {
			widgets.addTexture(this.getKillIcon(), 50, 19);
		}
		widgets.addText(Component.translatable(TranslatableStrings.TROPHY_DROP_CHANCE, TrophyRecipeViewerConstants.getTrophyDropPercentage(this.trophy())), 46, 45, 0xFF808080, false);
		widgets.addSlot(this.getOutputs().getFirst(), 81, 14).large(true).drawBack(false);
	}

	private EmiTexture getKillIcon() {
		return switch (TrophyConfig.trophyDropSource) {
			case ALL -> ANY_SOURCE_INDICATOR;
			case FAKE_PLAYER -> FAKE_PLAYER_INDICATOR;
			case PLAYER -> PLAYER_INDICATOR;
		};
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}
}
