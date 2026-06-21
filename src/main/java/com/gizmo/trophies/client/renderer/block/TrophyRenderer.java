package com.gizmo.trophies.client.renderer.block;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.client.PlayerInfoHolder;
import com.gizmo.trophies.client.PlayerTrophyModel;
import com.gizmo.trophies.client.renderer.TrophyRenderHelper;
import com.gizmo.trophies.client.renderer.state.TrophyState;
import com.gizmo.trophies.config.TrophyConfig;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerEarsModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class TrophyRenderer implements BlockEntityRenderer<TrophyBlockEntity, TrophyState> {

	@Deprecated //TODO create json system for future site thing
	private static final Map<String, Function<MutableComponent, MutableComponent>> SPECIAL_CASES = ImmutableMap.<String, Function<MutableComponent, MutableComponent>>builder()
		.put("celintro", name -> name.withStyle(ChatFormatting.GREEN).append(Component.literal(" ☠").withStyle(ChatFormatting.WHITE)))
		.put("fastcheeta", name -> name.withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal(" \uD83C\uDF3C").withStyle(ChatFormatting.LIGHT_PURPLE)))
		.put("derpderpling", name -> name.setStyle(Style.EMPTY.withColor(0x641ACF)))
		.put("bigdious", name -> name.withStyle(ChatFormatting.DARK_RED).append(Component.literal(" ☺")))
		.put("melodioustwin", name -> name.withStyle(ChatFormatting.DARK_AQUA).append(Component.literal(" ♫")))
		.put("badneighbour", name -> name.withStyle(ChatFormatting.YELLOW).append(Component.literal(" \uD83D\uDE97")))
		.put("jodlodi", name -> name.setStyle(Style.EMPTY.withColor(0x992D22)))
		.put("benimatic", name -> name.setStyle(Style.EMPTY.withColor(0x11806A)))
		.put("killer_demon", name -> name.withStyle(ChatFormatting.RED))
		.put("drullkus", name -> name.withStyle(ChatFormatting.GOLD))
		.put("tamaized", name -> name.setStyle(Style.EMPTY.withColor(0xFFA4EA)))
		.put("alphaleaf", name -> name.withStyle(ChatFormatting.GREEN))
		.put("memedreamxd", name -> name.withStyle(Style.EMPTY.withColor(0x48EBDC)))
		.build();

	private final PlayerTrophyModel trophy;
	private final PlayerTrophyModel slimTrophy;
	private final PlayerCapeModel cape;
	private final PlayerEarsModel ears;

	public TrophyRenderer(BlockEntityRendererProvider.Context context) {
		this.trophy = new PlayerTrophyModel(context.bakeLayer(ClientEvents.PLAYER_TROPHY), false);
		this.slimTrophy = new PlayerTrophyModel(context.bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true);
		this.cape = new PlayerCapeModel(context.bakeLayer(ModelLayers.PLAYER_CAPE));
		this.ears = new PlayerEarsModel(context.bakeLayer(ModelLayers.PLAYER_EARS));
	}

	@Override
	public void submit(TrophyState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		stack.pushPose();
		if (!state.blockState.getValue(TrophyBlock.PEDESTAL)) {
			stack.translate(0.0D, -0.25D, 0.0D);
		}
		if (state.trophy != null && state.entityState != null) {
			TrophyRenderHelper.renderTrophy(collector, state.entityState, cameraState, stack, state.blockPos, state.trophy, state.name, state.profile, -state.blockState.getValue(TrophyBlock.FACING).toYRot(), state.cycling, state.tickTimer, this.trophy, this.slimTrophy, this.cape, this.ears, state.lightCoords);
			if (TrophyConfig.playersRenderNames && state.distanceToCameraSq > 0.0D && state.profile != null && state.profile.name().isPresent()) {
				collector.submitNameTag(stack, new Vec3(0.5D, 0.85D, 0.5D), 0, handleSpecialNames(state.profile.name().get()), true, state.lightCoords, state.distanceToCameraSq, cameraState);
			}

		} else {
			stack.translate(0.5F, 0.85F, 0.5F);
			TrophyRenderHelper.renderNullDisplay(stack, collector, cameraState);
		}
		stack.popPose();
	}

	@Override
	public TrophyState createRenderState() {
		return new TrophyState();
	}

	@Override
	public void extractRenderState(TrophyBlockEntity entity, TrophyState state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
		if (entity.getTrophy() != null) {
			state.blockState = entity.getBlockState();
			state.trophy = entity.getTrophy();
			state.name = entity.getName();
			state.variant = entity.getVariant();
			state.cycling = entity.isCycling();
			state.tickTimer = entity.getLevel().getGameTime();
			state.entityState = TrophyRenderHelper.setupEntityState(state.trophy.type(), partialTick, entity.getLevel(), state.variant, state.trophy, state.name, state.cycling, entity.isBabyTrophy(), state.lightCoords);
			state.profile = entity.getPlayerProfile();
			if (Minecraft.getInstance().hitResult instanceof BlockHitResult result && result.getBlockPos().equals(entity.getBlockPos())) {
				state.distanceToCameraSq = cameraPosition.distanceToSqr(result.getLocation());
			} else {
				state.distanceToCameraSq = 0.0D;
			}
		}
	}

	private static Component handleSpecialNames(String name) {
		MutableComponent newName = Component.literal(name);
		if (name.equalsIgnoreCase("gizmothemoonpig")) {
			newName = newName.copy().setStyle(Style.EMPTY.withColor(0XFF3314)).append(Component.literal(" \uD83D\uDC51").withStyle(ChatFormatting.YELLOW));
		} else if (name.equalsIgnoreCase("tomatenjaeger")) {
			newName = newName.append(Component.literal(" ❤").withStyle(ChatFormatting.RED));
		}
		if (TrophyConfig.renderNameColorsAndIcons) {
			if (SPECIAL_CASES.containsKey(name.toLowerCase())) {
				newName = SPECIAL_CASES.get(name.toLowerCase()).apply(newName);
			}

			if (PlayerInfoHolder.TF_DEVS.contains(name.toLowerCase())) {
				newName = newName.append(Component.literal("\uE115").withStyle(ChatFormatting.WHITE));
			}

			if (PlayerInfoHolder.MOJANGSTAS.contains(name.toLowerCase())) {
				newName = newName.append(Component.literal("\uF56E").withStyle(ChatFormatting.DARK_RED));
			}
		}

		return newName;
	}
}
