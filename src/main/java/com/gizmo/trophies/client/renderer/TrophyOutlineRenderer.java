package com.gizmo.trophies.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;

public class TrophyOutlineRenderer implements CustomBlockOutlineRenderer {

	private final boolean renderBox;

	public TrophyOutlineRenderer(boolean renderBox) {
		this.renderBox = renderBox;
	}

	@Override
	public boolean render(BlockOutlineRenderState state, SubmitNodeCollector collector, PoseStack stack, LevelRenderState leveState) {
		return !this.renderBox;
	}
}
