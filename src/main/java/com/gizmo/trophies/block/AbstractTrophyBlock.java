package com.gizmo.trophies.block;

import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTrophyBlock extends BaseEntityBlock {

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty PEDESTAL = BooleanProperty.create("pedestal");
	protected static final VoxelShape PEDESTAL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 4.0D, 13.0D);
	protected static final VoxelShape NO_PEDESTAL_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

	public AbstractTrophyBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PEDESTAL, true));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return state.getValue(PEDESTAL) ? PEDESTAL_SHAPE : NO_PEDESTAL_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return state.getValue(PEDESTAL) ? PEDESTAL_SHAPE : Shapes.empty();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (player.isShiftKeyDown()) {
			level.setBlockAndUpdate(pos, state.cycle(PEDESTAL));
			level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drop = new ArrayList<>();
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof BlockEntity trophyBE) {
			ItemStack newStack = new ItemStack(this);
			newStack.applyComponents(trophyBE.collectComponents());
			newStack.set(DataComponents.RARITY, TrophyItem.getTrophyRarity(newStack));
			drop.add(newStack);
		}
		return drop;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader reader, BlockPos pos, BlockState state, boolean includeData, Player player) {
		ItemStack newStack = new ItemStack(this);
		if (reader.getBlockEntity(pos) instanceof BlockEntity trophyBE) {
			newStack.applyComponents(trophyBE.collectComponents());
			newStack.set(DataComponents.RARITY, TrophyItem.getTrophyRarity(newStack));
		}
		return newStack;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PEDESTAL);
	}
}
