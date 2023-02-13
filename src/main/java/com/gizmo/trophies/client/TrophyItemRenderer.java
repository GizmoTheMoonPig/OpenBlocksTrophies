package com.gizmo.trophies.client;

import com.gizmo.trophies.Registries;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TrophyItemRenderer extends BlockEntityWithoutLevelRenderer {
	public TrophyItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack ms, MultiBufferSource source, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof TrophyItem) {
			BakedModel base = Minecraft.getInstance().getBlockRenderer().getBlockModel(Registries.TROPHY.get().defaultBlockState());
			Minecraft.getInstance().getItemRenderer().renderModelLists(base, stack, light, overlay, ms, source.getBuffer(RenderType.solid()));

			if (stack.hasTag()) {
				Trophy trophy = TrophyItem.getTrophy(stack);
				if (trophy != null) {
					TrophyRenderer.renderEntity(null, Minecraft.getInstance().level, BlockPos.ZERO, trophy, ms, source, light, TrophyItem.hasCycleOnTrophy(stack));
				}
			}
		}
	}
}
