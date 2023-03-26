package com.gizmo.trophies.compat;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.TrophyConfig;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrophyCategory implements IRecipeCategory<TrophyInfoWrapper> {
	public static final int WIDTH = 116;
	public static final int HEIGHT = 54;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable fakePlayerIcon;
	private final IDrawable playerIcon;
	private final IDrawable arrowIcon;
	private final Component localizedName;

	public TrophyCategory(IGuiHelper helper) {
		ResourceLocation location = OpenBlocksTrophies.location("textures/gui/trophy_jei.png");
		this.background = helper.createDrawable(location, 0, 0, WIDTH, HEIGHT);
		this.fakePlayerIcon = helper.createDrawable(location, 116, 0, 16, 16);
		this.playerIcon = helper.createDrawable(location, 116, 16, 16, 16);
		this.arrowIcon = helper.createDrawable(location, 116, 32, 23, 15);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.DIAMOND_SWORD.getDefaultInstance());
		this.localizedName = Component.translatable("gui.obtrophies.trophy_jei");
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
	public void draw(TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		EntityRenderer.render(stack, recipe.getTrophyEntity(), 25, 42, recipe.getTrophyVariant(Minecraft.getInstance().level.registryAccess()));

		if (!TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get()) {
			if (TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get()) {
				this.fakePlayerIcon.draw(stack, 54, 19);
			} else {
				this.playerIcon.draw(stack, 54, 19);
			}
		} else {
			this.arrowIcon.draw(stack, 50, 19);
		}
		if (mouseX > 8 && mouseX < 43 && mouseY > 9 && mouseY < 44) {
			AbstractContainerScreen.renderSlotHighlight(stack, 10, 11, 0);
			AbstractContainerScreen.renderSlotHighlight(stack, 26, 11, 0);
			AbstractContainerScreen.renderSlotHighlight(stack, 10, 27, 0);
			AbstractContainerScreen.renderSlotHighlight(stack, 26, 27, 0);
		}
		Minecraft.getInstance().font.draw(stack, Component.translatable("gui.obtrophies.jei.drop_chance", recipe.getTrophyDropPercentage()), 46, 45, 0xFF808080);
	}

	@Override
	public List<Component> getTooltipStrings(TrophyInfoWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> components = new ArrayList<>();
		if (mouseX > 8 && mouseX < 43 && mouseY > 9 && mouseY < 44) {
			components.add(recipe.getTrophyEntity().getDescription());
			if (Minecraft.getInstance().options.advancedItemTooltips) {
				components.add(Component.literal(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(recipe.getTrophyEntity())).toString()).withStyle(ChatFormatting.DARK_GRAY));
			}
			components.add(Component.literal(this.getModIdForTooltip(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(recipe.getTrophyEntity())).getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
		}

		if (mouseX > 51 && mouseX < 73 && mouseY > 19 && mouseY < 34 && !TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get()) {
			components.add(Component.translatable("gui.obtrophies.jei.player_drops").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
			if (TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get()) {
				components.add(Component.translatable("gui.obtrophies.jei.fake_player_drops").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
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
		builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(new ItemStack(ForgeSpawnEggItem.fromEntityType(recipe.getTrophyEntity())));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 19).addIngredient(VanillaTypes.ITEM_STACK, recipe.getTrophyItem());
	}
}
