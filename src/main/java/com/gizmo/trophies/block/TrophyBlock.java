package com.gizmo.trophies.block;

import com.gizmo.trophies.misc.TrophyConfig;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.AmbientSoundFetcher;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import java.util.Objects;

@SuppressWarnings({"deprecation", "unchecked"})
public class TrophyBlock extends HorizontalDirectionalBlock implements EntityBlock {
	public static final MapCodec<TrophyBlock> CODEC = simpleCodec(TrophyBlock::new);

	public static final BooleanProperty PEDESTAL = BooleanProperty.create("pedestal");
	private static final VoxelShape PEDESTAL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 4.0D, 13.0D);
	private static final VoxelShape NO_PEDESTAL_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
	private static final VoxelShape PLAYER_SHAPE = Block.box(5.0D, 4.0D, 5.0D, 11.0D, 16.0D, 11.0D);

	public TrophyBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(PEDESTAL, true));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockAtPos, BlockEntityType<E> fedBlock, BlockEntityTicker<? super E> ticker) {
		return fedBlock == blockAtPos ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return state.getValue(PEDESTAL) ? PEDESTAL_SHAPE : Shapes.empty();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		if (getter.getBlockEntity(pos) instanceof TrophyBlockEntity trophy) {
			if (trophy.getTrophy() != null && trophy.getTrophy().type() == EntityType.PLAYER) {
				return state.getValue(PEDESTAL) ? Shapes.or(PEDESTAL_SHAPE, PLAYER_SHAPE) : NO_PEDESTAL_SHAPE;
			}
		}
		return state.getValue(PEDESTAL) ? PEDESTAL_SHAPE : NO_PEDESTAL_SHAPE;
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
		if (player.isShiftKeyDown()) {
			level.setBlockAndUpdate(pos, state.cycle(PEDESTAL));
			level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = trophyBE.getTrophy();
			if (trophy != null) {
				if (trophy.type() == EntityType.PLAYER) {
					level.playSound(null, pos, TrophyRegistries.OOF.get(), SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
				} else {
					Pair<SoundEvent, Float> soundData = AmbientSoundFetcher.getAmbientSoundAndPitch(trophy.type(), level);
					if (soundData.getFirst() != null) {
						level.playSound(null, pos, soundData.getFirst(), SoundSource.BLOCKS, 1.0F, soundData.getSecond());
					}
					if (trophyBE.getCooldown() <= 0 && trophy.clickBehavior().isPresent() && !TrophyConfig.COMMON_CONFIG.rightClickEffectOverride.get()) {
						trophyBE.setCooldown(trophy.clickBehavior().get().execute(trophyBE, (ServerPlayer) player, player.getItemInHand(hand)));
					}
				}
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof TrophyBlockEntity trophyBE && trophyBE.getTrophy() != null) {
			ItemStack newStack = new ItemStack(this);
			CompoundTag tag = new CompoundTag();
			tag.putString(TrophyItem.ENTITY_TAG, Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophyBE.getTrophy().type())).toString());
			tag.putInt(TrophyItem.VARIANT_TAG, trophyBE.getVariant());
			if (trophyBE.getCooldown() > 0) {
				tag.putInt(TrophyItem.COOLDOWN_TAG, trophyBE.getCooldown());
			}
			if (trophyBE.isCycling()) {
				tag.putBoolean(TrophyItem.CYCLING_TAG, true);
			}
			newStack.addTagElement("BlockEntityTag", tag);
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader reader, BlockPos pos, Player player) {
		ItemStack newStack = new ItemStack(this);
		CompoundTag tag = new CompoundTag();
		if (reader.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE && trophyBE.getTrophy() != null) {
			tag.putString("entity", Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophyBE.getTrophy().type())).toString());
			if (trophyBE.isCycling()) {
				tag.putBoolean(TrophyItem.CYCLING_TAG, true);
			}
			newStack.addTagElement("BlockEntityTag", tag);
			if (!trophyBE.getTrophyName().isEmpty()) {
				newStack.setHoverName(Component.literal(trophyBE.getTrophyName()));
			}
		}
		return newStack;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, PEDESTAL);
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
