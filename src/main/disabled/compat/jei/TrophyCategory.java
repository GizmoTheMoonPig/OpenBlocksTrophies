package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.compat.TrophyRecipeViewerConstants;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.misc.TranslatableStrings;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TrophyCategory implements IRecipeCategory<TrophyInfoWrapper> {
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable fakePlayerIcon;
	private final IDrawable playerIcon;
	private final IDrawable arrowIcon;
	private final Component localizedName;

	public TrophyCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TrophyRecipeViewerConstants.BACKGROUND, 0, 0, TrophyRecipeViewerConstants.WIDTH, TrophyRecipeViewerConstants.HEIGHT);
		this.fakePlayerIcon = helper.createDrawable(TrophyRecipeViewerConstants.BACKGROUND, 116, 0, 16, 16);
		this.playerIcon = helper.createDrawable(TrophyRecipeViewerConstants.BACKGROUND, 116, 16, 16, 16);
		this.arrowIcon = helper.createDrawable(TrophyRecipeViewerConstants.BACKGROUND, 116, 32, 23, 15);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.DIAMOND_SWORD.getDefaultInstance());
		this.localizedName = Component.translatable(TranslatableStrings.TROPHY_CATEGORY);
	}

	@Override
	public RecipeType<TrophyInfoWrapper> getRecipeType() {
		return JEICompat.TROPHY;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void draw(TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		TrophyRecipeViewerConstants.renderEntity(graphics, recipe.getTrophyEntity(), 25, 42, recipe.variant(), recipe.getDefaultTrophyVariant());

		switch (TrophyConfig.trophyDropSource) {
			case ALL -> this.arrowIcon.draw(graphics, 50, 19);
			case FAKE_PLAYER -> this.fakePlayerIcon.draw(graphics, 54, 19);
			case PLAYER -> this.playerIcon.draw(graphics, 54, 19);
		}
		if (mouseX > 8 && mouseX < 43 && mouseY > 9 && mouseY < 44) {
			AbstractContainerScreen.renderSlotHighlight(graphics, 10, 11, 0);
			AbstractContainerScreen.renderSlotHighlight(graphics, 26, 11, 0);
			AbstractContainerScreen.renderSlotHighlight(graphics, 10, 27, 0);
			AbstractContainerScreen.renderSlotHighlight(graphics, 26, 27, 0);
		}
		graphics.drawString(Minecraft.getInstance().font, Component.translatable(TranslatableStrings.TROPHY_DROP_CHANCE, TrophyRecipeViewerConstants.getTrophyDropPercentage(recipe.trophy())), 46, 45, 0xFF808080, false);
	}

	@Override
	public List<Component> getTooltipStrings(TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> components = new ArrayList<>();
		if (mouseX > 8 && mouseX < 43 && mouseY > 9 && mouseY < 44) {
			components.add(recipe.getTrophyEntity().getDescription());
			if (Minecraft.getInstance().options.advancedItemTooltips) {
				components.add(Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getTrophyEntity()).toString()).withStyle(ChatFormatting.DARK_GRAY));
			}
			components.add(Component.literal(this.getModIdForTooltip(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getTrophyEntity()).getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
		}

		if (mouseX > 51 && mouseX < 73 && mouseY > 19 && mouseY < 34 && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.ALL) {
			components.add(Component.translatable(TranslatableStrings.TROPHY_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			if (TrophyConfig.trophyDropSource == TrophyConfig.TrophySourceDrop.FAKE_PLAYER) {
				components.add(Component.translatable(TranslatableStrings.TROPHY_FAKE_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			}
		}
		return components;
	}

	private String getModIdForTooltip(String modId) {
		return ModList.get().getModContainerById(modId)
				.map(ModContainer::getModInfo)
				.map(IModInfo::getDisplayName)
				.orElseGet(() -> StringUtils.capitalize(modId));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, TrophyInfoWrapper recipe, IFocusGroup focuses) {
		SpawnEggItem egg = DeferredSpawnEggItem.byId(recipe.getTrophyEntity());
		if (egg != null) builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(new ItemStack(egg));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 19).addIngredient(VanillaTypes.ITEM_STACK, recipe.getTrophyItem());
	}
}
