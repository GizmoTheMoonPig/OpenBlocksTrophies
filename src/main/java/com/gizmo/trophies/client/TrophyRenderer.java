package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TrophyRenderer implements BlockEntityRenderer<TrophyBlockEntity> {

	private static final List<ResourceLocation> KEYS = new ArrayList<>();
	private final PlayerTrophyModel trophy;
	private final PlayerTrophyModel slimTrophy;
	private final PlayerCapeModel<?> cape;
	private final PlayerEarsModel ears;

	public TrophyRenderer(BlockEntityRendererProvider.Context context) {
		this.trophy = new PlayerTrophyModel(context.bakeLayer(ClientEvents.PLAYER_TROPHY), false);
		this.slimTrophy = new PlayerTrophyModel(context.bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true);
		this.cape = new PlayerCapeModel<>(context.bakeLayer(ModelLayers.PLAYER_CAPE));
		this.ears = new PlayerEarsModel(context.bakeLayer(ModelLayers.PLAYER_EARS));
	}

	public static void renderEntity(@Nullable TrophyBlockEntity be, CompoundTag variant, @Nullable Component name, Level level, BlockPos pos, Trophy trophy, PoseStack stack, MultiBufferSource source, int light, boolean cycling, PlayerTrophyModel normalTrophy, PlayerTrophyModel slimTrophy, PlayerCapeModel<?> cape, PlayerEarsModel ears) {
		if (name == null) name = Component.empty();

		stack.pushPose();
		if (KEYS.isEmpty() && !Trophy.getTrophies().isEmpty()) {
			KEYS.addAll(Trophy.getTrophies().keySet().stream().filter(location -> !location.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER))).toList());
			Collections.shuffle(KEYS);
		}
		if (trophy.type() == EntityType.PLAYER) {
			stack.translate(0.5F, 0.775F, 0.5F);
			if (be != null) {
				stack.mulPose(Axis.YP.rotationDegrees(-be.getBlockState().getValue(TrophyBlock.FACING).toYRot()));
			}
			if (name.getString().equalsIgnoreCase("dinnerbone") || name.getString().equalsIgnoreCase("grumm")) {
				stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
				stack.translate(0.0F, 0.295F, 0.0F);
			}
			stack.scale(0.35F, -0.35F, -0.35F);
			PlayerInfoHolder holder = PlayerInfoHolder.getSkinFromName(name.getString().toLowerCase(Locale.ROOT));

			if (holder.slim()) {
				slimTrophy.renderToBuffer(stack, source.getBuffer(holder.type()), light, OverlayTexture.NO_OVERLAY);
			} else {
				normalTrophy.renderToBuffer(stack, source.getBuffer(holder.type()), light, OverlayTexture.NO_OVERLAY);
			}
			if (holder.cape() != null) {
				stack.pushPose();
				cape.renderToBuffer(stack, source.getBuffer(RenderType.entitySolid(holder.cape())), light, OverlayTexture.NO_OVERLAY);
				stack.popPose();
			}
			if (name.getString().equalsIgnoreCase("deadmau5")) {
				stack.pushPose();
				stack.translate(0.0F, -0.1F, 0.05F);
				stack.scale(1.25F, 1.25F, 1.25F);
				ears.renderToBuffer(stack, source.getBuffer(holder.type()), light, OverlayTexture.NO_OVERLAY);
				stack.popPose();
			}
		} else {
			if (cycling && !KEYS.isEmpty()) {
				//use GLFW time to allow trophies to cycle in the advancements screen
				//level.getGameTime doesn't increment when the game is paused
				trophy = Trophy.getTrophies().get(KEYS.get((int) (Blaze3D.getTime() % KEYS.size())));
			}
			Entity entity = EntityCache.fetchEntity(trophy.type(), level, variant, trophy.defaultData());
			if (entity != null) {
				EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
				boolean hitboxes = dispatcher.shouldRenderHitBoxes();
				dispatcher.setRenderShadow(false);
				dispatcher.setRenderHitBoxes(false);
				entity.setCustomName(name);
				entity.setCustomNameVisible(false);
				//tick named sheep so the jeb_ name Easter Egg works properly. Lucky us the sheep doesn't need the tickCount for anything animation related so this works well.
				//I can't do this for every mob because mobs such as the blaze or pufferfish move when the tickCount is incremented, and I HATE moving trophies
				if (entity instanceof Sheep && entity.hasCustomName()) {
					entity.tickCount = (int) level.getLevelData().getGameTime();
				} else {
					entity.tickCount = 0;
				}
				entity.setPos(pos.getX() + 0.5D + trophy.offset().x(), pos.getY() + 0.25D + trophy.offset().y(), pos.getZ() + 0.5D + trophy.offset().z());
				stack.translate(0.5F + trophy.offset().x(), 0.25D + trophy.offset().y(), 0.5F + trophy.offset().z());
				if (be != null) {
					//they watch
					if (LocalDate.of(LocalDate.now().getYear(), 4, 1).equals(LocalDate.now())) {
						RandomSource rand = RandomSource.create(pos.asLong());
						if (rand.nextInt(10) == 0) {
							//they watch
							if (Minecraft.getInstance().cameraEntity != null) {
								Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
								Vec3 vec3 = new Vec3(camera.getPosition().x(), camera.getPosition().y(), camera.getPosition().z());
								Vec3 vec31 = new Vec3(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
								Vec3 vec32 = vec3.subtract(vec31);
								vec32 = vec32.normalize();
								float f5 = (float) Math.acos(vec32.y());
								float f6 = (float) Math.atan2(vec32.z(), vec32.x());
								stack.mulPose(Axis.YP.rotationDegrees((Mth.HALF_PI - f6) * Mth.RAD_TO_DEG));
								stack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG - 90.0F));
							}
						} else {
							//speen
							stack.mulPose(Axis.YP.rotationDegrees(level.getGameTime() * 15.0F));
						}
					} else {
						stack.mulPose(Axis.YP.rotationDegrees(-be.getBlockState().getValue(TrophyBlock.FACING).toYRot()));
					}
				}

				if (trophy.type() == EntityType.FOX && name.getString().equalsIgnoreCase("neoforge")) {
					stack.mulPose(Axis.YP.rotationDegrees(level.getGameTime() * 15.0F));
				}

				stack.mulPose(Axis.XP.rotationDegrees((float) trophy.rotation().x()));
				stack.mulPose(Axis.YP.rotationDegrees((float) trophy.rotation().y()));
				stack.mulPose(Axis.ZP.rotationDegrees((float) trophy.rotation().z()));

				stack.scale(0.4F, 0.4F, 0.4F);
				stack.scale(trophy.scale(), trophy.scale(), trophy.scale());

				try {
					dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, stack, source, light);
				} catch (Exception e) {
					OpenBlocksTrophies.LOGGER.error("Failed to render entity {} as a trophy", trophy.type().getDescriptionId(), e);
					EntityCache.addEntityToBlacklist(trophy.type());
				}
				dispatcher.setRenderShadow(true);
				dispatcher.setRenderHitBoxes(hitboxes);
			}
		}

		stack.popPose();
	}

	@Override
	public void render(TrophyBlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay, Vec3 cameraPos) {
		stack.pushPose();
		if (!blockEntity.getBlockState().getValue(TrophyBlock.PEDESTAL)) {
			stack.translate(0.0D, -0.25D, 0.0D);
		}
		if (blockEntity.getTrophy() != null) {
			renderEntity(blockEntity, blockEntity.getVariant(), blockEntity.getName(), blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getTrophy(), stack, source, light, blockEntity.isCycling(), this.trophy, this.slimTrophy, this.cape, this.ears);
		} else {
			stack.translate(0.5F, 0.85F, 0.5F);
			renderNullDisplay(stack, source);
		}
		stack.popPose();
	}

	public static void renderNullDisplay(PoseStack stack, MultiBufferSource source) {
		Quaternionf camRot = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
		stack.mulPose(new Quaternionf(0.0F, camRot.y, 0.0F, camRot.w));
		stack.scale(-0.075F, -0.075F, 0.075F);
		Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		stack.mulPose(Axis.YP.rotationDegrees(180));
		Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
	}

	public static void renderNullItemDisplay(PoseStack stack, MultiBufferSource source, boolean gui) {
		stack.translate(0.5F, 1.05F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(gui ? 135 : 0));
		stack.scale(-0.1F, -0.1F, 0.1F);
		Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		stack.mulPose(Axis.YP.rotationDegrees(180));
		Minecraft.getInstance().font.drawInBatch("?", -2.5F, 0, -1, false, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
	}
}
