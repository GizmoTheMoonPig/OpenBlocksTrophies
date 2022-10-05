package com.gizmo.trophies.block;

import com.gizmo.trophies.Registries;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.AmbientSoundFetcher;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"deprecation", "unchecked"})
public class TrophyBlock extends HorizontalDirectionalBlock implements EntityBlock {

	private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);

	public TrophyBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = TrophyItem.getTrophy(stack);
			if (trophy != null) {
				trophyBE.setTrophy(trophy);
				CompoundTag tag = BlockItem.getBlockEntityData(stack);
				if (tag != null && tag.contains(TrophyItem.COOLDOWN_TAG)) {
					trophyBE.setCooldown(tag.getInt(TrophyItem.COOLDOWN_TAG));
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
			Trophy trophy = trophyBE.getTrophy();
			if (trophy != null) {
				SoundEvent ambientSound = AmbientSoundFetcher.getAmbientSound(trophy.type(), level);
				if (ambientSound != null) {
					level.playSound(null, pos, ambientSound, SoundSource.BLOCKS, 1.0F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
				}
				if (trophyBE.getCooldown() <= 0 && trophy.behavior() != null) {
					trophyBE.setCooldown(trophy.behavior().execute(trophyBE, (ServerPlayer) player));
				}
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof TrophyBlockEntity trophyBE && trophyBE.getTrophy() != null) {
			ItemStack newStack = new ItemStack(this);
			CompoundTag tag = new CompoundTag();
			tag.putString(TrophyItem.ENTITY_TAG, Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(trophyBE.getTrophy().type())).toString());
			if (trophyBE.getCooldown() > 0) {
				tag.putInt(TrophyItem.COOLDOWN_TAG, trophyBE.getCooldown());
			}
			newStack.addTagElement("BlockEntityTag", tag);
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter getter, BlockPos pos, Player player) {
		ItemStack newStack = new ItemStack(this);
		CompoundTag tag = new CompoundTag();
		if (getter.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE && trophyBE.getTrophy() != null) {
			tag.putString("entity", Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(trophyBE.getTrophy().type())).toString());
			newStack.addTagElement("BlockEntityTag", tag);
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
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrophyBlockEntity(pos, state);
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockAtPos, BlockEntityType<E> fedBlock, BlockEntityTicker<? super E> ticker) {
		return fedBlock == blockAtPos ? (BlockEntityTicker<A>) ticker : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, Registries.TROPHY_BE.get(), (level1, pos, state1, blockEntity) -> TrophyBlockEntity.tick(blockEntity));
	}
}
