package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.compat.TrophyRecipeViewerConstants;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.misc.TranslatableStrings;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;

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
	public IRecipeType<TrophyInfoWrapper> getRecipeType() {
		return JEICompat.TROPHY;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public int getWidth() {
		return TrophyRecipeViewerConstants.WIDTH;
	}

	@Override
	public int getHeight() {
		return TrophyRecipeViewerConstants.HEIGHT;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void draw(TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		this.background.draw(graphics);
		TrophyRecipeViewerConstants.renderEntity(graphics, recipe.getTrophyEntity(), (int)graphics.pose().m20() + 10, (int)graphics.pose().m21() + 11, recipe.variant(), recipe.getDefaultTrophyVariant());

		switch (TrophyConfig.trophyDropSource) {
			case ALL -> this.arrowIcon.draw(graphics, 50, 19);
			case FAKE_PLAYER -> this.fakePlayerIcon.draw(graphics, 54, 19);
			case PLAYER -> this.playerIcon.draw(graphics, 54, 19);
		}
		if (mouseX > 9 && mouseX < 43 && mouseY > 10 && mouseY < 44) {
			graphics.fillGradient(10, 11, 42, 43, -2130706433, -2130706433);
		}
		graphics.text(Minecraft.getInstance().font, Component.translatable(TranslatableStrings.TROPHY_DROP_CHANCE, TrophyRecipeViewerConstants.getTrophyDropPercentage(recipe.trophy())), 46, 45, 0xFF808080, false);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX > 8 && mouseX < 43 && mouseY > 9 && mouseY < 44) {
			tooltip.addAll(TrophyRecipeViewerConstants.getMobTooltip(recipe.getTrophyEntity()));
			tooltip.add(Component.literal(TrophyRecipeViewerConstants.getModIdForTooltip(BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getTrophyEntity()).getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
		}

		if (mouseX > 51 && mouseX < 73 && mouseY > 19 && mouseY < 34 && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.ALL) {
			tooltip.add(TrophyRecipeViewerConstants.PLAYER_DROP_ONLY);
			if (TrophyConfig.trophyDropSource == TrophyConfig.TrophySourceDrop.FAKE_PLAYER) {
				tooltip.add(TrophyRecipeViewerConstants.FAKE_PLAYER_DROPS);
			}
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, TrophyInfoWrapper recipe, IFocusGroup focuses) {
		Optional<Holder<Item>> egg = SpawnEggItem.byId(recipe.getTrophyEntity());
		egg.ifPresent(itemHolder -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).add(itemHolder.value()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 19).add(recipe.getTrophyItem());
	}
}
