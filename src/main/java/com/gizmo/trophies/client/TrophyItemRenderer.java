package com.gizmo.trophies.client;

import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
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

			if (stack.hasTag()) {
				Trophy trophy = TrophyItem.getTrophy(stack);
				if (trophy != null && Minecraft.getInstance().level != null) {
					TrophyRenderer.renderEntity(null, TrophyItem.getTrophyVariant(stack), stack.hasCustomHoverName() ? stack.getHoverName().getString() : "", Minecraft.getInstance().level, BlockPos.ZERO, trophy, ms, source, light, TrophyItem.hasCycleOnTrophy(stack), this.trophy, this.slimTrophy);
				}
			}
		}
	}
}
