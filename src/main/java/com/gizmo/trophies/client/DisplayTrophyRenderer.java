package com.gizmo.trophies.client;

import com.gizmo.trophies.block.DisplayTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class DisplayTrophyRenderer implements BlockEntityRenderer<DisplayTrophyBlockEntity> {

	public DisplayTrophyRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(DisplayTrophyBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, entity.getBlockState().getValue(TrophyBlock.PEDESTAL) ? 0.5D : 0.25D, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(DisplayTrophyBlock.FACING).getOpposite().toYRot()));
		renderDisplay(entity.display.displayItem(), entity.display.scale(), entity.display.offset(), entity.display.rotation(), entity.display.rotationSpeed(), entity.display.bob(), entity.ticker + partialTicks, stack, source, light, overlay);
		stack.popPose();
	}

	public static void renderDisplay(Item displayItem, float scale, Vec3 offset, Vec3 rotation, float rotationSpeed, boolean bob, float ticker, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(offset.x, offset.y, offset.z);
		stack.mulPose(Axis.YP.rotationDegrees(ticker * rotationSpeed));
		stack.mulPose(new Quaternionf().rotateXYZ((float) (rotation.x() * Mth.DEG_TO_RAD), (float) (rotation.y() * Mth.DEG_TO_RAD), (float) (rotation.z() * Mth.DEG_TO_RAD)));
		if (bob) {
			stack.translate(0.0D, Mth.sin(ticker / 10.0F) * 0.1F + 0.1F, 0.0D);
		}
		stack.scale(0.4F, 0.4F, 0.4F);
		stack.scale(scale, scale, scale);
		Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(displayItem), ItemDisplayContext.NONE, light, overlay, stack, source, null, 0);
		stack.popPose();
	}
}
