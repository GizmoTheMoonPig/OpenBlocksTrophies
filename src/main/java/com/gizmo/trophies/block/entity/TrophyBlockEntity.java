package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TrophyRegistries;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TrophyBlockEntity extends BlockEntity {

	private int cooldown = 0;
	private TrophyInfo info;
	private Trophy cachedTrophy;
	@Nullable
	private Component name;

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
		if (this.cachedTrophy == null && this.info != null) {
			ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(this.info.type());
			this.cachedTrophy = Trophy.getTrophies().get(entityKey);
		}
		return this.cachedTrophy;
	}

	public void setTrophy(Trophy trophy) {
		this.cachedTrophy = trophy;
		this.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Nullable
	public Component getName() {
		return this.name;
	}

	public CompoundTag getVariant() {
		return this.info.variant().orElse(new CompoundTag());
	}

	public void setVariant(@Nullable CompoundTag variant) {
		this.info = this.info.withVariant(variant);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		output.storeNullable("name", ComponentSerialization.CODEC, this.name);
		if (this.info != null) {
			output.store("info", TrophyInfo.CODEC, this.info.withCooldown(this.cooldown));
		}
	}

	public boolean isCycling() {
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
		this.info = components.get(TrophyRegistries.TROPHY_INFO);
		this.name = components.get(DataComponents.CUSTOM_NAME);
		if (this.info != null) {
			this.cooldown = this.info.cooldown().orElse(0);
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(TrophyRegistries.TROPHY_INFO, this.info);
		components.set(DataComponents.CUSTOM_NAME, this.name);
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		output.discard("info");
		output.discard("name");
	}

	public void parseLegacyInfo(ValueInput input) {
		input.getString("entity").ifPresent(entity -> {
			if (Trophy.getTrophies().containsKey(ResourceLocation.tryParse(entity))) {
				this.setTrophy(Trophy.getTrophies().get(ResourceLocation.tryParse(entity)));
			}
		});
		this.setCooldown(input.getIntOr("cooldown", 0));

		input.getString("CustomNameEntity").ifPresent(name -> this.name = Component.literal(name));

		this.info = new TrophyInfo(this.getTrophy().type(), input.read("VariantID", CompoundTag.CODEC), Optional.empty(), Optional.of(this.getCooldown()));
	}
}
