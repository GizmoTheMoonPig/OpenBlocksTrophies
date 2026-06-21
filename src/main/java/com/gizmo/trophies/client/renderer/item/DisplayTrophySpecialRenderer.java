package com.gizmo.trophies.client.renderer.item;

import com.gizmo.trophies.client.renderer.TrophyRenderHelper;
import com.gizmo.trophies.client.renderer.block.DisplayTrophyRenderer;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public record DisplayTrophySpecialRenderer(Optional<Vec3> rotationOverride) implements SpecialModelRenderer<DataComponentMap> {

	@Override
	public DataComponentMap extractArgument(ItemStack stack) {
		return stack.getComponents();
	}

	@Override
	public void submit(@Nullable DataComponentMap map, PoseStack stack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
		DisplayTrophy trophy = TrophyHelper.getDisplayTrophy(map);
		stack.pushPose();
		if (trophy != null) {
			stack.translate(0.5F, 0.5F, 0.5F);
			ItemStackRenderState state = new ItemStackRenderState();
			Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, new ItemStack(trophy.displayItem()), ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 0);
			Vec3 rotation = this.rotationOverride().map(vec3 -> state.usesBlockLight() ? vec3.add(0.0F, 45.0F, 0.0F) : vec3).orElse(trophy.rotation());
			DisplayTrophyRenderer.renderDisplay(state, trophy.scale(), trophy.offset(), rotation, 0.0F, false, 0, stack, collector, packedLight);
		} else {
			TrophyRenderHelper.renderNullItemDisplay(stack, collector, this.rotationOverride());
		}
		stack.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> set) {
		//TODO...?
	}

	public record Unbaked(Optional<Vec3> rotationOverride) implements SpecialModelRenderer.Unbaked<DataComponentMap> {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Vec3.CODEC.optionalFieldOf("rotation_override").forGetter(Unbaked::rotationOverride)
		).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<DataComponentMap> bake(BakingContext set) {
			return new DisplayTrophySpecialRenderer(this.rotationOverride());
		}
	}
}
