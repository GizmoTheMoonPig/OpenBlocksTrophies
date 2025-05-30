package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayTrophyBlockEntity extends BlockEntity {

	public DisplayTrophy display;
	public int ticker;

	public DisplayTrophyBlockEntity(BlockPos pos, BlockState state) {
		super(TrophyRegistries.DISPLAY_TROPHY_BE.get(), pos, state);
	}

	public static void tick(DisplayTrophyBlockEntity be) {
		be.ticker++;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (this.display != null) {
			tag.put("display", DisplayTrophy.CODEC.encodeStart(NbtOps.INSTANCE, this.display).getOrThrow());
		}
		tag.putInt("tick", this.ticker);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		tag.getCompound("display").flatMap(display -> DisplayTrophy.CODEC.parse(NbtOps.INSTANCE, display).resultOrPartial()).ifPresent(parsedDisplay -> this.display = parsedDisplay);
		this.ticker = tag.getIntOr("tick", 0);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(TrophyRegistries.DISPLAY_TROPHY_INFO, this.display);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		this.display = components.getOrDefault(TrophyRegistries.DISPLAY_TROPHY_INFO, DisplayTrophy.FALLBACK);
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("display");
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
