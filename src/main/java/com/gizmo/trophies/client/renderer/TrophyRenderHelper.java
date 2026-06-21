package com.gizmo.trophies.client.renderer;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.client.EntityCache;
import com.gizmo.trophies.client.PlayerInfoHolder;
import com.gizmo.trophies.client.PlayerTrophyModel;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerEarsModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TrophyRenderHelper {

	private static final List<Identifier> KEYS = new ArrayList<>();

	public static void renderTrophy(SubmitNodeCollector collector, EntityRenderState state, CameraRenderState cameraState, PoseStack stack, BlockPos pos, Trophy trophy, @Nullable Component name, @Nullable ResolvableProfile profile, float rotation, boolean cycling, long timer, PlayerTrophyModel normal, PlayerTrophyModel slim, PlayerCapeModel cape, PlayerEarsModel ears, int light) {
		if (name == null) name = Component.empty();

		stack.pushPose();
		if (KEYS.isEmpty() && !Trophy.getTrophies().isEmpty()) {
			KEYS.addAll(Trophy.getTrophies().keySet().stream().filter(location -> !location.equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER))).toList());
			Collections.shuffle(KEYS);
		}
		if (trophy.type() == EntityType.PLAYER) {
			renderPlayer(collector, stack, rotation, profile, normal, slim, cape, ears, light);
		} else {
			renderEntity(collector, state, cameraState, stack, rotation, name, pos, trophy, cycling, timer);
		}

		stack.popPose();
	}

	public static void renderPlayer(SubmitNodeCollector collector, PoseStack stack, float rotation, @Nullable ResolvableProfile profile, PlayerTrophyModel normal, PlayerTrophyModel slim, PlayerCapeModel cape, PlayerEarsModel ears, int light) {
		stack.translate(0.5F, 0.775F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(rotation));

		PlayerInfoHolder holder = PlayerInfoHolder.getSkinFromProfile(profile);
		if (holder.upsideDown()) {
			stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			stack.translate(0.0F, 0.295F, 0.0F);
		}
		stack.scale(0.35F, -0.35F, -0.35F);

		collector.submitModel((holder.slim() ? slim : normal), new AvatarRenderState(), stack, holder.type(), light, OverlayTexture.NO_OVERLAY, 0, null);
		if (holder.cape() != null) {
			collector.submitModel(cape, new AvatarRenderState(), stack, RenderTypes.entityTranslucent(holder.cape()), light, OverlayTexture.NO_OVERLAY, 0, null);
		}
		if (profile != null && profile.name().map(s -> s.equalsIgnoreCase("deadmau5")).orElse(false)) {
			stack.pushPose();
			stack.translate(0.0F, -0.1F, 0.05F);
			stack.scale(1.25F, 1.25F, 1.25F);
			collector.submitModel(ears, new AvatarRenderState(), stack, holder.type(), light, OverlayTexture.NO_OVERLAY, 0, null);
			stack.popPose();
		}
	}

	public static EntityRenderState setupEntityState(EntityType<?> type, float partialTick, Level level, CompoundTag variant, Trophy trophy, @Nullable Component name, boolean cycling, boolean baby, int light) {
		if (cycling && !KEYS.isEmpty()) {
			type = Trophy.getTrophies().get(KEYS.get((int) (Blaze3D.getTime() % KEYS.size()))).type();
		}
		Entity entity = EntityCache.fetchEntity(type, level, variant, trophy.defaultData());
		if (entity != null) {
			entity.setCustomName(name);
			if (entity instanceof AgeableMob mob) {
				mob.setBaby(baby);
			}

			EntityRenderState state = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(entity, 0.0F);

			state.lightCoords = light;
			state.shadowPieces.clear();
			state.displayFireAnimation = false;

			if (entity instanceof Sheep && entity.hasCustomName()) {
				state.ageInTicks = (int) level.getLevelData().getGameTime() + partialTick;
			} else {
				state.ageInTicks = 0;
			}

			return state;
		}
		return new EntityRenderState();
	}

	public static void renderEntity(SubmitNodeCollector collector, EntityRenderState state, CameraRenderState cameraState, PoseStack stack, float rotation, Component name, BlockPos pos, Trophy trophy, boolean cycling, long timer) {
		if (cycling && !KEYS.isEmpty()) {
			//use GLFW time to allow trophies to cycle in the advancements screen
			//level.getGameTime doesn't increment when the game is paused
			trophy = Trophy.getTrophies().get(KEYS.get((int) (Blaze3D.getTime() % KEYS.size())));
		}

		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

		stack.translate(0.5F + trophy.offset().x(), 0.25D + trophy.offset().y(), 0.5F + trophy.offset().z());
		//they watch
		if (LocalDate.of(LocalDate.now().getYear(), 4, 1).equals(LocalDate.now())) {
			RandomSource rand = RandomSource.create(pos.asLong());
			if (rand.nextInt(10) == 0) {
				//they watch
				if (Minecraft.getInstance().getCameraEntity() != null) {
					Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
					Vec3 vec3 = new Vec3(camera.position().x(), camera.position().y(), camera.position().z());
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
				stack.mulPose(Axis.YP.rotationDegrees(timer * 15.0F));
			}
		} else {
			stack.mulPose(Axis.YP.rotationDegrees(rotation));
		}

		if (trophy.type() == EntityType.FOX && name.getString().equalsIgnoreCase("neoforge")) {
			stack.mulPose(Axis.YP.rotationDegrees(timer * 15.0F));
		}

		stack.mulPose(new Quaternionf().rotateXYZ(
			trophy.rotation().x() * Mth.DEG_TO_RAD,
			trophy.rotation().y() * Mth.DEG_TO_RAD,
			trophy.rotation().z() * Mth.DEG_TO_RAD));

		stack.scale(0.4F, 0.4F, 0.4F);
		stack.scale(trophy.scale(), trophy.scale(), trophy.scale());

		try {
			dispatcher.submit(state, cameraState, 0.0D, 0.0D, 0.0D, stack, collector);
		} catch (Exception e) {
			OpenBlocksTrophies.LOGGER.error("Failed to render entity {} as a trophy", trophy.type().getDescriptionId(), e);
			EntityCache.addEntityToBlacklist(trophy.type());
		}
	}

	public static void renderNullDisplay(PoseStack stack, SubmitNodeCollector collector, CameraRenderState state) {
		Quaternionf camRot = state.orientation;
		stack.mulPose(new Quaternionf(0.0F, camRot.y, 0.0F, camRot.w));
		stack.scale(-0.075F, -0.075F, 0.075F);
		collector.submitText(stack, -2.5F, 0, FormattedCharSequence.forward("?", Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, -1, 0, 0);
		stack.mulPose(Axis.YP.rotationDegrees(180));
		collector.submitText(stack, -2.5F, 0, FormattedCharSequence.forward("?", Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, -1, 0, 0);
	}

	public static void renderNullItemDisplay(PoseStack stack, SubmitNodeCollector collector, Optional<Vec3> override) {
		stack.translate(0.5F, 1.05F, 0.5F);
		Vector3f rotation = override.orElse(Vec3.ZERO).toVector3f();
		stack.mulPose(new Quaternionf().rotateXYZ(rotation.x, rotation.y, rotation.z));
		stack.scale(-0.1F, -0.1F, 0.1F);
		collector.submitText(stack, -2.5F, 0, FormattedCharSequence.forward("?", Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, -1, 0, 0);
		stack.mulPose(Axis.YP.rotationDegrees(180));
		collector.submitText(stack, -2.5F, 0, FormattedCharSequence.forward("?", Style.EMPTY), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, -1, 0, 0);
	}
}
