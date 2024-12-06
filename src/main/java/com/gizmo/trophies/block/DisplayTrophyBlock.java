package com.gizmo.trophies.block;

import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisplayTrophyBlock extends AbstractTrophyBlock {

	public static final MapCodec<DisplayTrophyBlock> CODEC = simpleCodec(DisplayTrophyBlock::new);

	public DisplayTrophyBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DisplayTrophyBlockEntity trophy) {
			if (trophy.display.rightClickSound().isPresent()) {
				level.playSound(null, pos, trophy.display.rightClickSound().get(), SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
				return InteractionResult.SUCCESS;
			}
		}
		return super.useWithoutItem(state, level, pos, player, result);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof DisplayTrophyBlockEntity trophy) {
			ItemStack newStack = new ItemStack(this);
			newStack.set(TrophyRegistries.DISPLAY_TROPHY_INFO, trophy.display);
			newStack.set(DataComponents.RARITY, trophy.display.displayItem().getDefaultInstance().getRarity());
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		ItemStack newStack = new ItemStack(this);
		if (level.getBlockEntity(pos) instanceof DisplayTrophyBlockEntity trophy) {
			newStack.set(TrophyRegistries.DISPLAY_TROPHY_INFO, trophy.display);
			newStack.set(DataComponents.RARITY, trophy.display.displayItem().getDefaultInstance().getRarity());
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
