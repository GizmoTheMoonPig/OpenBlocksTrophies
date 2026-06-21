package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.function.Supplier;

public class VariantToggleButton extends Button {

	private static final WidgetSprites SPRITES = new WidgetSprites(
		OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_selected.png"),
		OpenBlocksTrophies.prefix("textures/gui/sprites/check_box.png"),
		OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_selected_hovered.png"),
		OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_hovered.png")
	);
	private boolean selected;

	public VariantToggleButton(int x, int y, Component message, boolean selected, Button.OnPress press) {
		super(x, y, 12, 12, message, press, Supplier::get);
		this.selected = selected;
		this.setTooltip(Tooltip.create(message));
	}

	@Override
	public void onPress(InputWithModifiers modifiers) {
		this.selected = !this.selected;
		super.onPress(modifiers);
	}

	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				output.add(NarratedElementType.USAGE, Component.translatable(this.selected ? "narration.checkbox.usage.focused.uncheck" : "narration.checkbox.usage.focused.check"));
			} else {
				output.add(NarratedElementType.USAGE, Component.translatable(this.selected ? "narration.checkbox.usage.hovered.uncheck" : "narration.checkbox.usage.hovered.check"));
			}
		}
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.selected, this.isHoveredOrFocused()), this.getX(), this.getY(), 0, 0, 14, 14, 14, 14, ARGB.white(this.alpha));
	}
}
