package com.gizmo.trophies.compat;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.client.EntityCache;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Optional;

public class EntityRenderer {

	public static void render(PoseStack stack, @Nullable EntityType<?> type, int x, int y, @Nullable CompoundTag variant, Optional<CompoundTag> defaultVariant) {
		if (type != null) {
			LivingEntity entity = EntityCache.fetchEntity(type, Minecraft.getInstance().level, variant, defaultVariant);
			if (entity != null) {
				int scale = 16;
				float height = entity.getBbHeight();
				float width = entity.getBbWidth();
				if (height <= 0.5F && width < 0.75F) {
					scale = (int) (Math.max(height, width) * 48);
				} else if (height < 1.0F && width < 0.75F) {
					scale = (int) (Math.max(height, width) * 32);
				} else if (height > 2.0F) {
					scale = (int) (32 / Math.max(height, width));
				}

				// catch exceptions drawing the entity to be safe, any caught exceptions blacklist the entity
				try {
					PoseStack modelView = RenderSystem.getModelViewStack();
					modelView.pushPose();
					modelView.mulPoseMatrix(stack.last().pose());
					renderTheEntity(x, y, scale, entity);
					modelView.popPose();
					RenderSystem.applyModelViewMatrix();
				} catch (Exception e) {
					OpenBlocksTrophies.LOGGER.error("Error drawing entity " + BuiltInRegistries.ENTITY_TYPE.getKey(type), e);
					EntityCache.addEntityToBlacklist(type);
				}
			}
		}
	}

	//[VanillaCopy] of InventoryScreen.renderEntityInInventory, with added rotations and some other modified values
	public static void renderTheEntity(int x, int y, int scale, LivingEntity entity) {
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate(x, y, 1050.0D);
		applyAdditionalTransforms(entity.getType(), posestack);
		posestack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		posestack1.translate(0.0D, 0.0D, 1000.0D);
		if (entity instanceof EnderDragon) {
			posestack1.mulPose(Axis.YP.rotationDegrees(180.0F));
			posestack1.mulPose(Axis.XP.rotationDegrees(-30.0F));
		}
		posestack1.scale((float) scale, (float) scale, (float) scale);
		Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
		Quaternionf quaternion1 = Axis.XP.rotationDegrees(20.0F);
		quaternion.mul(quaternion1);
		posestack1.mulPose(quaternion);
		posestack1.mulPose(Axis.XN.rotationDegrees(35.0F));
		posestack1.mulPose(Axis.YN.rotationDegrees(145.0F));
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 0.0F;
		entity.setYRot(0.0F);
		entity.setXRot(0.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternion1.conjugate();
		dispatcher.overrideCameraOrientation(quaternion1);
		boolean hitboxes = dispatcher.shouldRenderHitBoxes();
		dispatcher.setRenderShadow(false);
		dispatcher.setRenderHitBoxes(false);
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, posestack1, multibuffersource$buffersource, 15728880));
		multibuffersource$buffersource.endBatch();
		dispatcher.setRenderShadow(true);
		dispatcher.setRenderHitBoxes(hitboxes);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}

	//certain entities are a pain. This exists to fix vanilla cases.
	private static void applyAdditionalTransforms(EntityType<?> entity, PoseStack stack) {
		if (entity == EntityType.GHAST) {
			stack.translate(0.0D, -12.5D, 0.0D);
			stack.scale(0.5F, 0.5F, 0.5F);
		}
		if (entity == EntityType.ENDER_DRAGON) stack.translate(0.0D, -4.0D, 0.0D);
		if (entity == EntityType.WITHER) stack.translate(0.0D, 8.0D, 0.0D);
		if (entity == EntityType.SQUID || entity == EntityType.GLOW_SQUID) stack.translate(0.0D, -19.0D, 0.0D);
		if (entity == EntityType.ELDER_GUARDIAN) stack.scale(0.6F, 0.6F, 0.6F);
	}
}
