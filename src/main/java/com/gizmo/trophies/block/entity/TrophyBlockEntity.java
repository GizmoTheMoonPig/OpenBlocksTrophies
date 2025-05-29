package com.gizmo.trophies.block.entity;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
		if (this.cachedTrophy == null) {
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
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.saveAdditional(tag, provider);
		if (tag.contains("name", 8)) {
			this.name = parseCustomNameSafe(tag.getString("name"), provider);
		}
		if (this.info != null) {
			tag.put("info", TrophyInfo.CODEC.encodeStart(NbtOps.INSTANCE, this.info.withCooldown(this.cooldown)).getOrThrow());
		}
	}

	public boolean isCycling() {
		return this.info.cycling().isPresent();
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.loadAdditional(tag, provider);
		if (tag.contains("entity")) {
			this.parseLegacyInfo(tag);
		} else {
			if (this.name != null) {
				tag.putString("name", Component.Serializer.toJson(this.name, provider));
			}
			TrophyInfo.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("info")).resultOrPartial(OpenBlocksTrophies.LOGGER::error).ifPresent(info -> {
				this.info = info;
				this.setCooldown(info.cooldown().orElse(0));
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
	protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		this.info = componentInput.get(TrophyRegistries.TROPHY_INFO);
		this.name = componentInput.get(DataComponents.CUSTOM_NAME);
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
	public void removeComponentsFromTag(CompoundTag tag) {
		tag.remove("info");
		tag.remove("name");
	}

	public void parseLegacyInfo(CompoundTag tag) {
		if (Trophy.getTrophies().containsKey(ResourceLocation.tryParse(tag.getString("entity")))) {
			this.setTrophy(Trophy.getTrophies().get(ResourceLocation.tryParse(tag.getString("entity"))));
		}
		this.setCooldown(tag.getInt("cooldown"));

		Optional<CompoundTag> variant = Optional.empty();
		if (tag.contains("VariantID")) {
			variant = Optional.of(tag.getCompound("VariantID"));
		}

		if (tag.contains("CustomNameEntity")) {
			this.name = Component.literal(tag.getString("CustomNameEntity"));
		}

		this.info = new TrophyInfo(this.getTrophy().type(), variant, Optional.empty(), Optional.of(this.getCooldown()));
	}
}
