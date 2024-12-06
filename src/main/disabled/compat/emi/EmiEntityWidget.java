package com.gizmo.trophies.compat.emi;

import com.gizmo.trophies.compat.TrophyRecipeViewerConstants;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmiEntityWidget extends Widget {
	private final EntityType<?> type;
	private final int size;
	private final Bounds bounds;
	private final CompoundTag variant;
	private final Optional<CompoundTag> defaultVariant;

	public EmiEntityWidget(EntityType<?> type, int x, int y, int size, CompoundTag variant, Optional<CompoundTag> defaultVariant) {
		this.type = type;
		this.size = size;
		this.bounds = new Bounds(x, y, size, size);
		this.variant = variant;
		this.defaultVariant = defaultVariant;
	}

	@Override
	public Bounds getBounds() {
		return this.bounds;
	}

	@Override
	public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
		List<ClientTooltipComponent> tooltip = new ArrayList<>();
		TrophyRecipeViewerConstants.getMobTooltip(this.type).forEach(component -> tooltip.add(ClientTooltipComponent.create(component.getVisualOrderText())));
		tooltip.add(ClientTooltipComponent.create(Component.literal(TrophyRecipeViewerConstants.getModIdForTooltip(BuiltInRegistries.ENTITY_TYPE.getKey(this.type).getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC).getVisualOrderText()));
		return tooltip;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.pose().pushPose();
		graphics.pose().translate(this.bounds.x() + this.size / 2.0F, this.bounds.y() + this.size - 1, 0.0D);
		TrophyRecipeViewerConstants.renderEntity(graphics, this.type, 0, 0, this.variant, this.defaultVariant);
		graphics.pose().popPose();
	}
}
