package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.init.TrophyBlockEntities;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class TrophyBlockEntity extends BlockEntity {

	private int cooldown = 0;
	@Nullable
	private TrophyInfo info;
	@Nullable
	private Trophy cachedTrophy;
	@Nullable
	private Component name;
	@Nullable
	private ResolvableProfile playerProfile;

	public TrophyBlockEntity(BlockPos pos, BlockState state) {
		super(TrophyBlockEntities.TROPHY.get(), pos, state);
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
		if (this.cachedTrophy == null && this.info != null) {
			Identifier entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(this.info.type());
			this.cachedTrophy = Trophy.getTrophies().get(entityKey);
		}
		return this.cachedTrophy;
	}

	public void setTrophy(Trophy trophy) {
		this.cachedTrophy = trophy;
		this.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	public void setInfo(@Nullable TrophyInfo info) {
		this.info = info;
	}

	public boolean isBabyTrophy() {
		return this.info != null && this.info.baby();
	}

	public boolean cycleBaby() {
		if (this.info != null) {
			this.info = this.info.setBaby(!this.info.baby());
			this.setChanged();
			if (this.getLevel() != null) {
				this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
			}
			return this.info.baby();
		}
		return false;
	}

	@Nullable
	public Component getName() {
		return this.name;
	}

	public void setName(@Nullable Component name) {
		this.name = name;
	}

	public CompoundTag getVariant() {
		if (this.info == null) return new CompoundTag();
		return this.info.variant().orElse(new CompoundTag());
	}

	public void setVariant(@Nullable CompoundTag variant) {
		if (this.info == null) return;
		this.info = this.info.withVariant(variant);
	}

	@Nullable
	public ResolvableProfile getPlayerProfile() {
		return this.playerProfile;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		output.storeNullable("name", ComponentSerialization.CODEC, this.name);
		if (this.info != null) {
			output.storeNullable("info", TrophyInfo.CODEC, this.info.withCooldown(this.cooldown));
		}
		output.storeNullable("profile", ResolvableProfile.CODEC, this.playerProfile);
	}

	public boolean isCycling() {
		if (this.info == null) return false;
		return this.info.cycling().isPresent();
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		if (input.getString("entity").isPresent()) {
			this.parseLegacyInfo(input);
		} else {
			this.name = parseCustomNameSafe(input, "name");
			input.read("info", TrophyInfo.CODEC).ifPresent(parsedInfo -> {
				this.info = parsedInfo;
				this.setCooldown(parsedInfo.cooldown().orElse(0));
			});
			this.playerProfile = input.read("profile", ResolvableProfile.CODEC).orElse(null);
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		return this.saveWithoutMetadata(provider);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		this.info = components.get(TrophyComponents.TROPHY_INFO);
		this.name = components.get(DataComponents.CUSTOM_NAME);
		this.playerProfile = components.get(DataComponents.PROFILE);
		if (this.info != null) {
			this.cooldown = this.info.cooldown().orElse(0);
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(TrophyComponents.TROPHY_INFO, this.info);
		components.set(DataComponents.CUSTOM_NAME, this.name);
		components.set(DataComponents.PROFILE, this.playerProfile);
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		output.discard("info");
		output.discard("name");
		output.discard("profile");
	}

	public void parseLegacyInfo(ValueInput input) {
		input.getString("entity").ifPresent(entity -> {
			if (Trophy.getTrophies().containsKey(Identifier.tryParse(entity))) {
				this.setTrophy(Trophy.getTrophies().get(Identifier.tryParse(entity)));
			}
		});
		this.setCooldown(input.getIntOr("cooldown", 0));

		input.getString("CustomNameEntity").ifPresent(name -> this.name = Component.literal(name));

		this.info = new TrophyInfo(this.getTrophy().type(), input.read("VariantID", CompoundTag.CODEC), Optional.empty(), Optional.of(this.getCooldown()), false);
	}
}
