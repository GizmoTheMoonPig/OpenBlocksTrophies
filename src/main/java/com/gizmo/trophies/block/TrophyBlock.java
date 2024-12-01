package com.gizmo.trophies.block;

import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.AmbientSoundFetcher;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrophyBlock extends AbstractTrophyBlock {
	public static final MapCodec<TrophyBlock> CODEC = simpleCodec(TrophyBlock::new);
	private static final VoxelShape PLAYER_SHAPE = Block.box(5.0D, 4.0D, 5.0D, 11.0D, 16.0D, 11.0D);

	public TrophyBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		if (getter.getBlockEntity(pos) instanceof TrophyBlockEntity trophy) {
			if (trophy.getTrophy() != null && trophy.getTrophy().type() == EntityType.PLAYER) {
				return state.getValue(PEDESTAL) ? Shapes.or(PEDESTAL_SHAPE, PLAYER_SHAPE) : NO_PEDESTAL_SHAPE;
			}
		}
		return super.getShape(state, getter, pos, context);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = TrophyItem.getTrophy(stack);
			if (trophy != null) {
				trophyBE.setTrophy(trophy);
				trophyBE.setTrophyName(stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName().getString() : "");
				TrophyInfo info = stack.get(TrophyRegistries.TROPHY_INFO);
				if (info != null) {
					if (info.variant().isPresent()) {
						trophyBE.setVariant(info.variant().get());
					}
					if (info.cooldown().isPresent()) {
						trophyBE.setCooldown(info.cooldown().get());
					}

					if (info.cooldown().isPresent()) {
						trophyBE.setCycling(true);
					}
				}
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = trophyBE.getTrophy();
			if (trophy != null) {
				if (trophy.type() == EntityType.PLAYER) {
					level.playSound(null, pos, TrophyRegistries.OOF.get(), SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
				} else {
					if (trophy.clickSoundOverride().isPresent()) {
						level.playSound(null, pos, trophy.clickSoundOverride().get(), SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
					} else {
						Pair<SoundEvent, Float> soundData = AmbientSoundFetcher.getAmbientSoundAndPitch(trophy.type(), level);
						if (soundData.getFirst() != null) {
							level.playSound(null, pos, soundData.getFirst(), SoundSource.BLOCKS, 1.0F, soundData.getSecond());
						}
						if (trophyBE.getCooldown() <= 0 && trophy.clickBehavior().isPresent() && !TrophyConfig.rightClickEffectOverride) {
							trophyBE.setCooldown(trophy.clickBehavior().get().execute(trophyBE, (ServerPlayer) player, player.getItemInHand(hand)));
						}
					}
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, result);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (player.isShiftKeyDown()) {
			level.setBlockAndUpdate(pos, state.cycle(PEDESTAL));
			level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof TrophyBlockEntity trophyBE) {
			ItemStack newStack = new ItemStack(this);
			newStack.set(TrophyRegistries.TROPHY_INFO, TrophyInfo.makeFromBlock(trophyBE));
			newStack.set(DataComponents.RARITY, TrophyItem.getTrophyRarity(newStack));
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader reader, BlockPos pos, Player player) {
		ItemStack newStack = new ItemStack(this);
		if (reader.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
			newStack.set(TrophyRegistries.TROPHY_INFO, TrophyInfo.makeFromBlock(trophyBE));
			if (!trophyBE.getTrophyName().isEmpty()) {
				newStack.set(DataComponents.ITEM_NAME, Component.literal(trophyBE.getTrophyName()));
			}
			newStack.set(DataComponents.RARITY, TrophyItem.getTrophyRarity(newStack));
		}
		return newStack;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrophyBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, TrophyRegistries.TROPHY_BE.get(), (level1, pos, state1, blockEntity) -> TrophyBlockEntity.tick(blockEntity));
	}
}
