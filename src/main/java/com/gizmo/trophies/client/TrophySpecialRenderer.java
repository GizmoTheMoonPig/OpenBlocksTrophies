package com.gizmo.trophies.client;

import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TrophySpecialRenderer implements SpecialModelRenderer<DataComponentMap> {

	private final PlayerTrophyModel trophy;
	private final PlayerTrophyModel slimTrophy;
	private final PlayerCapeModel<?> cape;
	private final PlayerEarsModel ears;

	public TrophySpecialRenderer(PlayerTrophyModel normal, PlayerTrophyModel slim, PlayerCapeModel<?> cape, PlayerEarsModel ears) {
		this.trophy = normal;
		this.slimTrophy = slim;
		this.cape = cape;
		this.ears = ears;
	}

	@Override
	public @Nullable DataComponentMap extractArgument(ItemStack stack) {
		return stack.getComponents();
	}

	@Override
	public void render(@Nullable DataComponentMap map, ItemDisplayContext context, PoseStack stack, MultiBufferSource buffer, int light, int overlay, boolean foil) {
		Trophy trophy = TrophyItem.getTrophy(map);
		if (trophy != null && Minecraft.getInstance().level != null) {
			TrophyRenderer.renderEntity(null, TrophyItem.getTrophyVariant(map), map.get(DataComponents.CUSTOM_NAME), Minecraft.getInstance().level, BlockPos.ZERO, trophy, stack, buffer, light, TrophyItem.hasCycleOnTrophy(map), this.trophy, this.slimTrophy, this.cape, this.ears);
		} else {
			TrophyRenderer.renderNullItemDisplay(stack, buffer, context == ItemDisplayContext.GUI);
		}
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final TrophySpecialRenderer.Unbaked INSTANCE = new TrophySpecialRenderer.Unbaked();
		public static final MapCodec<TrophySpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public MapCodec<TrophySpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(EntityModelSet set) {
			return new TrophySpecialRenderer(
				new PlayerTrophyModel(set.bakeLayer(ClientEvents.PLAYER_TROPHY), false),
				new PlayerTrophyModel(set.bakeLayer(ClientEvents.SLIM_PLAYER_TROPHY), true),
				new PlayerCapeModel<>(set.bakeLayer(ModelLayers.PLAYER_CAPE)),
				new PlayerEarsModel(set.bakeLayer(ModelLayers.PLAYER_EARS)));
		}
	}
}
