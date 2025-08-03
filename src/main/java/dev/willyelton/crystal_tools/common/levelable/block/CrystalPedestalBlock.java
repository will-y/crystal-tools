package dev.willyelton.crystal_tools.common.levelable.block;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CrystalPedestalBlock extends BaseEntityBlock {
    public static final MapCodec<CrystalPedestalBlock> CODEC = simpleCodec(CrystalPedestalBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(4, 4, 15, 12, 12, 16),
            Block.box(5, 5, 14, 11, 11, 15),
            Block.box(6, 6, 11, 10, 10, 14),
            Block.box(5, 5, 10, 11, 11, 11),
            Block.box(4, 4, 9, 12, 12, 10),
            Block.box(4, 4, 8, 6, 6, 9),
            Block.box(4, 10, 8, 6, 12, 9),
            Block.box(10, 4, 8, 12, 6, 9),
            Block.box(10, 10, 8, 12, 12, 9)
    );

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(4, 4, 0, 12, 12, 1),
            Block.box(5, 5, 1, 11, 11, 2),
            Block.box(6, 6, 2, 10, 10, 5),
            Block.box(5, 5, 5, 11, 11, 6),
            Block.box(4, 4, 6, 12, 12, 7),
            Block.box(4, 4, 7, 6, 6, 8),
            Block.box(4, 10, 7, 6, 12, 8),
            Block.box(10, 4, 7, 12, 6, 8),
            Block.box(10, 10, 7, 12, 12, 8)
    );

    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(0, 4, 4, 1, 12, 12),
            Block.box(1, 5, 5, 2, 11, 11),
            Block.box(2, 6, 6, 5, 10, 10),
            Block.box(5, 5, 5, 6, 11, 11),
            Block.box(6, 4, 4, 7, 12, 12),
            Block.box(7, 4, 4, 8, 6, 6),
            Block.box(7, 10, 4, 8, 12, 6),
            Block.box(7, 4, 10, 8, 6, 12),
            Block.box(7, 10, 10, 8, 12, 12)
    );

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(15, 4, 4, 16, 12, 12),
            Block.box(14, 5, 5, 15, 11, 11),
            Block.box(11, 6, 6, 14, 10, 10),
            Block.box(10, 5, 5, 11, 11, 11),
            Block.box(9, 4, 4, 10, 12, 12),
            Block.box(8, 4, 4, 9, 6, 6),
            Block.box(8, 10, 4, 9, 12, 6),
            Block.box(8, 4, 10, 9, 6, 12),
            Block.box(8, 10, 10, 9, 12, 12)
    );

    private static final VoxelShape SHAPE_UP = Shapes.or(
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(5, 1, 5, 11, 2, 11),
            Block.box(6, 2, 6, 10, 5, 10),
            Block.box(5, 5, 5, 11, 6, 11),
            Block.box(4, 6, 4, 12, 7, 12),
            Block.box(4, 7, 4, 6, 8, 6),
            Block.box(4, 7, 10, 6, 8, 12),
            Block.box(10, 7, 4, 12, 8, 6),
            Block.box(10, 7, 10, 12, 8, 12)
    );

    private static final VoxelShape SHAPE_DOWN = Shapes.or(
            Block.box(4, 15, 4, 12, 16, 12),
            Block.box(5, 14, 5, 11, 15, 11),
            Block.box(6, 11, 6, 10, 14, 10),
            Block.box(5, 10, 5, 11, 11, 11),
            Block.box(4, 9, 4, 12, 10, 12),
            Block.box(4, 8, 4, 6, 9, 6),
            Block.box(4, 8, 10, 6, 9, 12),
            Block.box(10, 8, 4, 12, 9, 6),
            Block.box(10, 8, 10, 12, 9, 12)
    );

    private final Map<Direction, VoxelShape> shapes;

    public CrystalPedestalBlock(Properties properties) {
        super(properties);

        this.shapes = Map.of(Direction.NORTH, SHAPE_NORTH,
                Direction.SOUTH, SHAPE_SOUTH,
                Direction.EAST, SHAPE_EAST,
                Direction.WEST, SHAPE_WEST,
                Direction.UP, SHAPE_UP,
                Direction.DOWN, SHAPE_DOWN);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof CrystalPedestalBlockEntity blockEntity) {
                    blockEntity.clientTick(lvl, pos, blockState);
                }
            };
        }

        return (lvl, pos, blockState, t) -> {
            if (t instanceof CrystalPedestalBlockEntity blockEntity) {
                blockEntity.serverTick(level, blockEntity.getBlockPos(), blockState);
            }
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalPedestalBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof CrystalPedestalBlockEntity crystalPedestalBlockEntity) {

                if (player.isShiftKeyDown()) {
                    this.openContainer(level, pos, player);
                    return ItemInteractionResult.CONSUME;
                }

                ItemStack result = crystalPedestalBlockEntity.setStack(stack);

                player.setItemInHand(hand, result);

                return ItemInteractionResult.CONSUME;
            }

            return ItemInteractionResult.FAIL;
        }
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrystalPedestalBlockEntity crystalPedestalBlockEntity && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(crystalPedestalBlockEntity, registryFriendlyByteBuf -> registryFriendlyByteBuf.writeBlockPos(pos));
        } else {
            throw new IllegalStateException("Crystal pedestal block doesn't have a block entity!");
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState()
                .setValue(FACING, blockPlaceContext.getClickedFace());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof CrystalPedestalBlockEntity crystalPedestalBlockEntity) {
            crystalPedestalBlockEntity.dropContents(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_324584_) {
        return RenderShape.MODEL;
    }
}
