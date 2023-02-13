package com.gizmo.trophies.block;

import com.gizmo.trophies.Registries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TrophyBlockEntity extends BlockEntity {

	private int cooldown = 0;
	private Trophy trophy;
	private boolean specialCycleVariant = false;

	public TrophyBlockEntity(BlockPos pos, BlockState state) {
		super(Registries.TROPHY_BE.get(), pos, state);
	}

	public static void tick(TrophyBlockEntity be) {
		if (be.cooldown > 0) be.cooldown--;
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	@Nullable
	public Trophy getTrophy() {
		return this.trophy;
	}

	public void setTrophy(Trophy trophy) {
		this.trophy = trophy;
		this.markUpdated();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (this.getTrophy() != null) {
			tag.putString("entity", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(this.getTrophy().type())).toString());
		}
		tag.putInt("cooldown", this.getCooldown());
		if (this.specialCycleVariant) {
			tag.putBoolean("SpecialCycleVariant", true);
		}
	}

	public boolean isCycling() {
		return this.specialCycleVariant;
	}

	public void setCycling(boolean cycling) {
		this.specialCycleVariant = cycling;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (Trophy.getTrophies().containsKey(ResourceLocation.tryParse(tag.getString("entity")))) {
			this.setTrophy(Trophy.getTrophies().get(ResourceLocation.tryParse(tag.getString("entity"))));
		}
		this.setCooldown(tag.getInt("cooldown"));
		if (tag.contains("SpecialCycleVariant")) {
			this.specialCycleVariant = tag.getBoolean("SpecialCycleVariant");
		}
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

	private void markUpdated() {
		this.setChanged();

		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);

			this.getLevel().updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
			this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 2);
		}
	}

	private void updateClient() {
		if (this.getLevel() != null && this.getLevel().isClientSide()) {
			this.requestModelDataUpdate();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}
}
