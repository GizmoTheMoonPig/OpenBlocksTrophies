package com.gizmo.trophies.client.renderer.state;

import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TrophyState extends BlockEntityRenderState {

	public BlockState blockState = Blocks.AIR.defaultBlockState();
	@Nullable
	public Component name;
	@Nullable
	public EntityRenderState entityState;
	@Nullable
	public Trophy trophy;
	public CompoundTag variant = new CompoundTag();
	public boolean cycling;
	public long tickTimer;
	@Nullable
	public ResolvableProfile profile;
	public double distanceToCameraSq = 0.0D;
}
