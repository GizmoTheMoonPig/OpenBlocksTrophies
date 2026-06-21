package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.init.TrophyBlockEntities;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class DisplayTrophyBlockEntity extends BlockEntity {

	@Nullable
	public DisplayTrophy display;
	public int ticker;

	public DisplayTrophyBlockEntity(BlockPos pos, BlockState state) {
		super(TrophyBlockEntities.DISPLAY_TROPHY.get(), pos, state);
	}

	public static void tick(DisplayTrophyBlockEntity be) {
		be.ticker++;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.storeNullable("display", DisplayTrophy.CODEC, this.display);
		output.putInt("tick", this.ticker);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		input.read("display", DisplayTrophy.CODEC).ifPresent(parsedDisplay -> this.display = parsedDisplay);
		this.ticker = input.getIntOr("tick", 0);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(TrophyComponents.DISPLAY_TROPHY_INFO, this.display);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		this.display = components.getOrDefault(TrophyComponents.DISPLAY_TROPHY_INFO, DisplayTrophy.FALLBACK);
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		output.discard("display");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		return this.saveCustomOnly(provider);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
