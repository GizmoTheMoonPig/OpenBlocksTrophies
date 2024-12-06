package com.gizmo.trophies.client;

import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DisplayTrophySpecialRenderer implements SpecialModelRenderer<DataComponentMap> {

	@Override
	public @Nullable DataComponentMap extractArgument(ItemStack stack) {
		return stack.getComponents();
	}

	@Override
	public void render(@Nullable DataComponentMap map, ItemDisplayContext context, PoseStack stack, MultiBufferSource buffer, int light, int overlay, boolean foil) {
		BakedModel base = Minecraft.getInstance().getBlockRenderer().getBlockModel(TrophyRegistries.DISPLAY_TROPHY.get().defaultBlockState());
		ItemRenderer.renderItem(context, stack, buffer, light, overlay, new int[0], base, RenderType.solid(), foil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);

		DisplayTrophy trophy = DisplayTrophyItem.getTrophy(map);
		if (trophy != null) {
			stack.pushPose();
			stack.translate(0.5F, 0.5F, 0.5F);
			stack.mulPose(Axis.YP.rotationDegrees(180));
			DisplayTrophyRenderer.renderDisplay(trophy.displayItem(), trophy.scale(), trophy.offset(), trophy.rotation(), 0.0F, false, 0, stack, buffer, light, overlay);
			stack.popPose();
		} else {
			stack.pushPose();
			stack.translate(0.5F, 1.05F, 0.5F);
			stack.mulPose(Axis.YP.rotationDegrees(135));
			stack.scale(-0.1F, -0.1F, 0.1F);
			Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
			stack.mulPose(Axis.YP.rotationDegrees(180));
			Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
			stack.popPose();
		}
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
