package com.gizmo.trophies.client;

import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TrophyItemRenderer extends BlockEntityWithoutLevelRenderer {

	private PlayerTrophyModel trophy = new PlayerTrophyModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientEvents.PLAYER_TROPHY), false);
	private PlayerTrophyModel slimTrophy = new PlayerTrophyModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true);

	public TrophyItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		super.onResourceManagerReload(manager);
		this.trophy = new PlayerTrophyModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientEvents.PLAYER_TROPHY), false);
		this.slimTrophy = new PlayerTrophyModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack ms, MultiBufferSource source, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof TrophyItem) {
			BakedModel base = Minecraft.getInstance().getBlockRenderer().getBlockModel(TrophyRegistries.TROPHY.get().defaultBlockState());
			Minecraft.getInstance().getItemRenderer().renderModelLists(base, stack, light, overlay, ms, source.getBuffer(RenderType.solid()));

			if (stack.has(TrophyRegistries.TROPHY_INFO)) {
				Trophy trophy = TrophyItem.getTrophy(stack);
				if (trophy != null && Minecraft.getInstance().level != null) {
					TrophyRenderer.renderEntity(null, TrophyItem.getTrophyVariant(stack), stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.empty(), Minecraft.getInstance().level, BlockPos.ZERO, trophy, ms, source, light, TrophyItem.hasCycleOnTrophy(stack), this.trophy, this.slimTrophy);
				}
			}
		} else if (item instanceof DisplayTrophyItem) {
			BakedModel base = Minecraft.getInstance().getBlockRenderer().getBlockModel(TrophyRegistries.TROPHY.get().defaultBlockState());
			Minecraft.getInstance().getItemRenderer().renderModelLists(base, stack, light, overlay, ms, source.getBuffer(RenderType.solid()));

			if (stack.has(TrophyRegistries.DISPLAY_TROPHY_INFO)) {
				DisplayTrophy trophy = DisplayTrophyItem.getTrophy(stack);
				if (trophy != null) {
					ms.pushPose();
					ms.translate(0.5F, 0.5F, 0.5F);
					ms.mulPose(Axis.YP.rotationDegrees(180));
					DisplayTrophyRenderer.renderDisplay(trophy.displayItem(), trophy.scale(), trophy.offset(), trophy.rotation(), 0.0F, false, 0, ms, source, light, overlay);
					ms.popPose();
				}
			} else {
				ms.pushPose();
				ms.translate(0.5F, 1.05F, 0.5F);
				ms.mulPose(Axis.YP.rotationDegrees(135));
				ms.scale(-0.1F, -0.1F, 0.1F);
				Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, ms.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
				ms.mulPose(Axis.YP.rotationDegrees(180));
				Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, ms.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
				ms.popPose();
			}
		}
	}
}
