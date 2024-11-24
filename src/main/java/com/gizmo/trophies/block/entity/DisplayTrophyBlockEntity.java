package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DisplayTrophyBlockEntity extends BlockEntity {

	public DisplayTrophy display = DisplayTrophy.FALLBACK;
	public int ticker;

	public DisplayTrophyBlockEntity(BlockPos pos, BlockState state) {
		super(TrophyRegistries.DISPLAY_TROPHY_BE.get(), pos, state);
	}

	public static void tick(DisplayTrophyBlockEntity be) {
		be.ticker++;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("display", DisplayTrophy.CODEC.encodeStart(NbtOps.INSTANCE, this.display).getOrThrow(false, OpenBlocksTrophies.LOGGER::error));
		tag.putInt("tick", this.ticker);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.display = DisplayTrophy.CODEC.parse(NbtOps.INSTANCE, tag.get("display")).getOrThrow(false, OpenBlocksTrophies.LOGGER::error);
		this.ticker = tag.getInt("tick");
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		this.handleUpdateTag(Objects.requireNonNull(pkt.getTag()));
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		this.updateClient();
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithId();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, tile -> this.getUpdateTag());
	}

	private void updateClient() {
		if (this.getLevel() != null && this.getLevel().isClientSide()) {
			this.requestModelDataUpdate();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

}
