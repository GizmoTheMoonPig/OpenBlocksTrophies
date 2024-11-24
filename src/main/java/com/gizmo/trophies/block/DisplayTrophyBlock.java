package com.gizmo.trophies.block;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.DisplayTrophy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisplayTrophyBlock extends AbstractTrophyBlock {

	public DisplayTrophyBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = TrophyItem.getTrophy(stack);
			if (trophy != null) {
				trophyBE.setTrophy(trophy);
				trophyBE.setTrophyName(stack.hasCustomHoverName() ? stack.getHoverName().getString() : "");
				CompoundTag tag = BlockItem.getBlockEntityData(stack);
				if (tag != null) {
					if (tag.contains(TrophyItem.COOLDOWN_TAG)) {
						trophyBE.setCooldown(tag.getInt(TrophyItem.COOLDOWN_TAG));
					}

					if (tag.contains(TrophyItem.CYCLING_TAG)) {
						trophyBE.setCycling(tag.getBoolean(TrophyItem.CYCLING_TAG));
					}
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DisplayTrophyBlockEntity trophy) {
			if (trophy.display.rightClickSound().isPresent()) {
				level.playSound(null, pos, trophy.display.rightClickSound().get(), SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.use(state, level, pos, player, hand, result);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof DisplayTrophyBlockEntity trophy) {
			ItemStack newStack = new ItemStack(this);
			CompoundTag tag = new CompoundTag();
			tag.put("display", DisplayTrophy.CODEC.encodeStart(NbtOps.INSTANCE, trophy.display).getOrThrow(false, OpenBlocksTrophies.LOGGER::error));
			newStack.addTagElement("BlockEntityTag", tag);
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter getter, BlockPos pos, Player player) {
		ItemStack newStack = new ItemStack(this);
		CompoundTag tag = new CompoundTag();
		if (getter.getBlockEntity(pos) instanceof DisplayTrophyBlockEntity trophy) {
			tag.put("display", DisplayTrophy.CODEC.encodeStart(NbtOps.INSTANCE, trophy.display).getOrThrow(false, OpenBlocksTrophies.LOGGER::error));
			newStack.addTagElement("BlockEntityTag", tag);
		}
		return newStack;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DisplayTrophyBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide() ? null : createTickerHelper(type, TrophyRegistries.DISPLAY_TROPHY_BE.get(), (level1, pos, state1, blockEntity) -> DisplayTrophyBlockEntity.tick(blockEntity));
	}
}
