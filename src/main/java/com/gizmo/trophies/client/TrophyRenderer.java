package com.gizmo.trophies.client;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.Objects;

public class TrophyRenderer implements BlockEntityRenderer<TrophyBlockEntity> {

	private static final LoadingCache<EntityContext<?>, Entity> ENTITY_CACHE = CacheBuilder.newBuilder().build(
			new CacheLoader<>() {
				@Override
				public Entity load(EntityContext<?> context) {
					return context.createEntity();
				}
			}
	);

	public TrophyRenderer(BlockEntityRendererProvider.Context unused) {
	}

	public static void renderEntity(@Nullable TrophyBlockEntity be, Level level, BlockPos pos, Trophy trophy, PoseStack stack, MultiBufferSource source, int light) {
		stack.pushPose();
		Entity entity = fetchEntity(trophy.type(), Objects.requireNonNull(level));
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		dispatcher.setRenderShadow(false);
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
					stack.mulPose(Vector3f.YP.rotationDegrees((Mth.HALF_PI - f6) * Mth.RAD_TO_DEG));
					stack.mulPose(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI) - 90.0F));
				}
			} else {
				stack.mulPose(Vector3f.YP.rotationDegrees(getCorrectRotation(be.getBlockState().getValue(TrophyBlock.FACING).getOpposite())));
			}
		}

		if (entity instanceof EnderDragon) {
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		}

		stack.scale(0.4F, 0.4F, 0.4F);
		stack.scale(trophy.scale(), trophy.scale(), trophy.scale());
		if (TrophyExtraRendering.getRenderMap().containsKey(trophy.type())) {
			TrophyExtraRendering.getRenderForEntity(trophy.type()).createExtraRender(entity);
		}

		dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, stack, source, light);
		dispatcher.setRenderShadow(false);
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

	@Nullable
	public static <T extends Entity> Entity fetchEntity(@Nullable EntityType<T> type, Level level) {
		if (type == null)
			return null;
		return ENTITY_CACHE.getUnchecked(EntityContext.of(type, level));
	}

	@Override
	public void render(TrophyBlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (blockEntity.getTrophy() != null) {
			stack.pushPose();
			if (!blockEntity.getBlockState().getValue(TrophyBlock.PEDESTAL)) {
				stack.translate(0.0D, -0.25D, 0.0D);
			}
			renderEntity(blockEntity, blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getTrophy(), stack, source, light);
			stack.popPose();
		}
	}

	public static record EntityContext<T extends Entity>(EntityType<T> type, Level level) {
		public static <T extends Entity> EntityContext<T> of(EntityType<T> type, Level level) {
			return new EntityContext<>(type, level);
		}

		public T createEntity() {
			return Objects.requireNonNull(this.type.create(this.level));
		}
	}
}
