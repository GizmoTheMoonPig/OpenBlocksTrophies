package com.gizmo.trophies.client;

import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class DisplayTrophySpecialRenderer implements SpecialModelRenderer<DataComponentMap> {

	@Override
	public @Nullable DataComponentMap extractArgument(ItemStack stack) {
		return stack.getComponents();
	}

	@Override
	public void render(@Nullable DataComponentMap map, ItemDisplayContext context, PoseStack stack, MultiBufferSource buffer, int light, int overlay, boolean foil) {
		DisplayTrophy trophy = DisplayTrophyItem.getTrophy(map);
		stack.pushPose();
		if (trophy != null) {
			stack.translate(0.5F, 0.5F, 0.5F);
			stack.mulPose(Axis.YP.rotationDegrees(180));
			DisplayTrophyRenderer.renderDisplay(trophy.displayItem(), trophy.scale(), trophy.offset(), trophy.rotation(), 0.0F, false, 0, stack, buffer, light, overlay);
		} else {
			TrophyRenderer.renderNullItemDisplay(stack, buffer, context == ItemDisplayContext.GUI);
		}
		stack.popPose();
	}

	@Override
	public void getExtents(Set<Vector3f> set) {
		//TODO...?
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final Unbaked INSTANCE = new Unbaked();
		public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(EntityModelSet set) {
			return new DisplayTrophySpecialRenderer();
		}
	}
}
