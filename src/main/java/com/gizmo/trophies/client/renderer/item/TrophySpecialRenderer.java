package com.gizmo.trophies.client.renderer.item;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.client.PlayerTrophyModel;
import com.gizmo.trophies.client.renderer.TrophyRenderHelper;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerEarsModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public record TrophySpecialRenderer(Optional<Vec3> rotationOverride, PlayerTrophyModel normal, PlayerTrophyModel slim, PlayerCapeModel cape, PlayerEarsModel ears) implements SpecialModelRenderer<DataComponentMap> {

	@Override
	public DataComponentMap extractArgument(ItemStack stack) {
		return stack.getComponents();
	}

	@Override
	public void submit(@Nullable DataComponentMap map, PoseStack stack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outlineColor) {
		if (map != null) {
			TrophyInfo info = map.get(TrophyComponents.TROPHY_INFO);
			Trophy trophy = TrophyHelper.getTrophy(info);
			if (trophy != null && Minecraft.getInstance().level != null) {
				EntityRenderState state = TrophyRenderHelper.setupEntityState(info.type(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), Minecraft.getInstance().level, TrophyHelper.getTrophyVariant(info), trophy, map.get(DataComponents.CUSTOM_NAME), TrophyHelper.hasCycleOnTrophy(info), info.baby(), light);
				TrophyRenderHelper.renderTrophy(collector, state, new CameraRenderState(), stack, BlockPos.ZERO, trophy, map.get(DataComponents.CUSTOM_NAME), map.get(DataComponents.PROFILE), 0.0F, TrophyHelper.hasCycleOnTrophy(info), 0L, this.normal(), this.slim(), this.cape(), this.ears(), light);
			} else {
				TrophyRenderHelper.renderNullItemDisplay(stack, collector, this.rotationOverride());
			}
		}
	}

	@Override
	public void getExtents(Consumer<Vector3fc> set) {
		PoseStack stack = new PoseStack();
		this.normal().root().getExtentsForGui(stack, set);
		this.ears().root().getExtentsForGui(stack, set);
		this.cape().root().getExtentsForGui(stack, set);
	}

	public record Unbaked(Optional<Vec3> rotationOverride) implements SpecialModelRenderer.Unbaked<DataComponentMap> {

		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Vec3.CODEC.optionalFieldOf("rotation_override").forGetter(Unbaked::rotationOverride)
		).apply(instance, Unbaked::new));

		@Override
		public MapCodec<TrophySpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<DataComponentMap> bake(BakingContext context) {
			return new TrophySpecialRenderer(
				this.rotationOverride(),
				new PlayerTrophyModel(context.entityModelSet().bakeLayer(ClientEvents.PLAYER_TROPHY), false),
				new PlayerTrophyModel(context.entityModelSet().bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true),
				new PlayerCapeModel(context.entityModelSet().bakeLayer(ModelLayers.PLAYER_CAPE)),
				new PlayerEarsModel(context.entityModelSet().bakeLayer(ModelLayers.PLAYER_EARS)));
		}
	}
}
