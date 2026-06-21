package com.gizmo.trophies.client.renderer.block;

import com.gizmo.trophies.block.DisplayTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.client.renderer.TrophyRenderHelper;
import com.gizmo.trophies.client.renderer.state.DisplayTrophyState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class DisplayTrophyRenderer implements BlockEntityRenderer<DisplayTrophyBlockEntity, DisplayTrophyState> {

	private final ItemModelResolver resolver;

	public DisplayTrophyRenderer(BlockEntityRendererProvider.Context context) {
		this.resolver = context.itemModelResolver();
	}

	@Override
	public void submit(DisplayTrophyState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		stack.pushPose();
		stack.translate(0.5F, state.blockState.getValue(TrophyBlock.PEDESTAL) ? 0.5D : 0.25D, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-state.blockState.getValue(DisplayTrophyBlock.FACING).toYRot()));
		if (state.display != null && !state.itemState.isEmpty()) {
			renderDisplay(state.itemState, state.display.scale(), state.display.offset(), state.display.rotation(), state.display.rotationSpeed(), state.display.bob(), state.rotationTicks, stack, collector, state.lightCoords);
		} else {
			stack.translate(0.0F, 0.35F, 0.0F);
			TrophyRenderHelper.renderNullDisplay(stack, collector, cameraState);
		}
		stack.popPose();
	}

	public static void renderDisplay(ItemStackRenderState itemState, float scale, Vec3 offset, Vec3 rotation, float rotationSpeed, boolean bob, float ticker, PoseStack stack, SubmitNodeCollector collector, int light) {
		stack.pushPose();
		stack.translate(offset.x, offset.y, offset.z);
		stack.mulPose(Axis.YP.rotationDegrees(ticker * rotationSpeed));
		stack.mulPose(new Quaternionf().rotateXYZ((float) (rotation.x() * Mth.DEG_TO_RAD), (float) (rotation.y() * Mth.DEG_TO_RAD), (float) (rotation.z() * Mth.DEG_TO_RAD)));
		if (bob) {
			stack.translate(0.0D, Mth.sin(ticker / 10.0F) * 0.1F + 0.1F, 0.0D);
		}
		stack.scale(0.4F, 0.4F, 0.4F);
		stack.scale(scale, scale, scale);
		itemState.submit(stack, collector, light, OverlayTexture.NO_OVERLAY, 0);
		stack.popPose();
	}

	@Override
	public DisplayTrophyState createRenderState() {
		return new DisplayTrophyState();
	}

	@Override
	public void extractRenderState(DisplayTrophyBlockEntity entity, DisplayTrophyState state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
		state.blockState = entity.getBlockState();
		state.display = entity.display;
		state.rotationTicks = entity.ticker + partialTick;
		if (entity.display != null) {
			this.resolver.updateForTopItem(state.itemState, new ItemStack(entity.display.displayItem()), ItemDisplayContext.NONE, entity.getLevel(), null, 0);
		}
	}
}
