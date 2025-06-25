package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.function.Supplier;

public class VariantToggleButton extends Button {
	private static final ResourceLocation CHECKBOX_SELECTED_HOVERED_SPRITE = OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_selected_hovered.png");
	private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_selected.png");
	private static final ResourceLocation CHECKBOX_HOVERED_SPRITE = OpenBlocksTrophies.prefix("textures/gui/sprites/check_box_hovered.png");
	private static final ResourceLocation CHECKBOX_SPRITE = OpenBlocksTrophies.prefix("textures/gui/sprites/check_box.png");

	private boolean selected;

	public VariantToggleButton(int x, int y, Component message, boolean selected, Button.OnPress press) {
		super(x, y, 12, 12, message, press, Supplier::get);
		this.selected = selected;
		this.setTooltip(Tooltip.create(message));
	}

	@Override
	public void onPress() {
		this.selected = !this.selected;
		super.onPress();
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
			} else {
				output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
			}
		}
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int x, int y, float partialTicks) {
		ResourceLocation resourcelocation;
		if (this.selected) {
			resourcelocation = this.isHovered() ? CHECKBOX_SELECTED_HOVERED_SPRITE : CHECKBOX_SELECTED_SPRITE;
		} else {
			resourcelocation = this.isHovered() ? CHECKBOX_HOVERED_SPRITE : CHECKBOX_SPRITE;
		}
		graphics.blit(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getX(), this.getY(), 0, 0, 14, 14, 14, 14, ARGB.white(this.alpha));
	}
}
