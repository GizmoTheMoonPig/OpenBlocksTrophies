package com.gizmo.trophies.block;

import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TrophyBlockEntity extends BlockEntity {

	private int cooldown = 0;
	private int variant = 0;
	private Trophy trophy;
	private String trophyName = "";
	private boolean specialCycleVariant = false;

	public TrophyBlockEntity(BlockPos pos, BlockState state) {
		super(TrophyRegistries.TROPHY_BE.get(), pos, state);
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

	public int getVariant() {
		return this.variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public String getTrophyName() {
		return this.trophyName;
	}

	public void setTrophyName(String trophyName) {
		this.trophyName = trophyName;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (this.getTrophy() != null) {
			tag.putString("entity", Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(this.getTrophy().type())).toString());
		}
		tag.putInt("cooldown", this.getCooldown());
		if (this.specialCycleVariant) {
			tag.putBoolean("SpecialCycleVariant", true);
		}
		tag.putInt("VariantID", this.getVariant());
		if (!this.getTrophyName().isEmpty()) {
			tag.putString("CustomNameEntity", this.getTrophyName());
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
		this.variant = tag.getInt("VariantID");
		if (tag.contains("CustomNameEntity")) {
			this.setTrophyName(tag.getString("CustomNameEntity"));
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
