package com.gizmo.trophies.client.renderer.state;

import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class DisplayTrophyState extends BlockEntityRenderState {

	public BlockState blockState = Blocks.AIR.defaultBlockState();
	public final ItemStackRenderState itemState = new ItemStackRenderState();
	@Nullable
	public DisplayTrophy display;
	public float rotationTicks;
}
