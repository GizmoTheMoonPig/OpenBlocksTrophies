package com.gizmo.trophies.client;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrophyRenderer implements BlockEntityRenderer<TrophyBlockEntity> {

	private static final List<ResourceLocation> keys = new ArrayList<>();

	public TrophyRenderer(BlockEntityRendererProvider.Context unused) {
	}

	public static void renderEntity(@Nullable TrophyBlockEntity be, int variant, String name, Level level, BlockPos pos, Trophy trophy, PoseStack stack, MultiBufferSource source, int light, boolean cycling) {
		stack.pushPose();
		if (keys.isEmpty() && !Trophy.getTrophies().isEmpty()) {
			keys.addAll(Trophy.getTrophies().keySet().stream().toList());
			Collections.shuffle(keys);
		}
		if (cycling && !keys.isEmpty()) {
			//use GLFW time to allow trophies to cycle in the advancements screen
			//level.getGameTime doesn't increment when the game is paused
			trophy = Trophy.getTrophies().get(keys.get((int) (Blaze3D.getTime() % keys.size())));
		}
		List<CompoundTag> variants = trophy.getVariants(level.registryAccess());
		Entity entity = EntityCache.fetchEntity(trophy.type(), level, variant < variants.size() ? variants.get(variant) : null, trophy.defaultData());
		if (entity != null) {
			EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
			boolean hitboxes = dispatcher.shouldRenderHitBoxes();
			dispatcher.setRenderShadow(false);
			dispatcher.setRenderHitBoxes(false);
			entity.setCustomName(!name.isEmpty() ? Component.literal(name) : null);
			entity.setCustomNameVisible(false);
			//tick named sheep so the jeb_ name Easter Egg works properly. Lucky us the sheep doesn't need the tickCount for anything animation related so this works well.
			//I can't do this for every mob because mobs such as the blaze or pufferfish move when the tickCount is incremented, and I HATE moving trophies
			if (entity instanceof Sheep && entity.hasCustomName()) {
				entity.tickCount = (int) level.getLevelData().getGameTime();
			} else {
				entity.tickCount = 0;
			}
			entity.setPos(pos.getX() + 0.5D, pos.getY() + 0.25D + trophy.verticalOffset(), pos.getZ() + 0.5D);
			stack.translate(0.5F, 0.25D + trophy.verticalOffset(), 0.5F);
			if (be != null) {
				//they watch
				if (LocalDate.of(LocalDate.now().getYear(), 4, 1).equals(LocalDate.now())) {
					Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64.0D, false);
					if (player != null) {
						Vec3 vec3 = new Vec3(player.getX(), player.getEyeY(), player.getZ());
						Vec3 vec31 = new Vec3(pos.getX(), pos.getY() + (entity.getEyeHeight() / 1.5), pos.getZ());
						Vec3 vec32 = vec3.subtract(vec31);
						vec32 = vec32.normalize();
						float f5 = (float) Math.acos(vec32.y());
						float f6 = (float) Math.atan2(vec32.z(), vec32.x());
						stack.mulPose(Axis.YP.rotationDegrees((Mth.HALF_PI - f6) * Mth.RAD_TO_DEG));
						stack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float) Math.PI) - 90.0F));
					}
				} else {
					stack.mulPose(Axis.YP.rotationDegrees(getCorrectRotation(be.getBlockState().getValue(TrophyBlock.FACING).getOpposite())));
				}
			}

			if (entity instanceof EnderDragon) {
				stack.mulPose(Axis.YP.rotationDegrees(180.0F));
			}

			stack.scale(0.4F, 0.4F, 0.4F);
			stack.scale(trophy.scale(), trophy.scale(), trophy.scale());

			RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, stack, source, light));
			dispatcher.setRenderShadow(true);
			dispatcher.setRenderHitBoxes(hitboxes);
		}

		stack.popPose();
	}

	private static float getCorrectRotation(Direction direction) {
		return switch (direction) {
			case DOWN, UP, NORTH -> 0.0F;
			case SOUTH -> 180.0F;
			case EAST -> 270.0F;
			case WEST -> 90.0F;
		};
	}

	@Override
	public void render(TrophyBlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (blockEntity.getTrophy() != null) {
			stack.pushPose();
			if (!blockEntity.getBlockState().getValue(TrophyBlock.PEDESTAL)) {
				stack.translate(0.0D, -0.25D, 0.0D);
			}
			renderEntity(blockEntity, blockEntity.getVariant(), blockEntity.getTrophyName(), blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getTrophy(), stack, source, light, blockEntity.isCycling());
			stack.popPose();
		}
	}
}
