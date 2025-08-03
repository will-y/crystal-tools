package dev.willyelton.crystal_tools.common.levelable.block;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
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

    private static final VoxelShape SHAPE = Shapes.or(
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

    private final Map<Direction, VoxelShape> shapes;

    public CrystalPedestalBlock(Properties properties) {
        super(properties);

        this.shapes = Shapes.rotateAll(SHAPE);
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
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof CrystalPedestalBlockEntity crystalPedestalBlockEntity) {

                if (player.isShiftKeyDown()) {
                    this.openContainer(level, pos, player);
                    return InteractionResult.CONSUME;
                }

                ItemStack result = crystalPedestalBlockEntity.setStack(stack);

                player.setItemInHand(hand, result);

                return InteractionResult.CONSUME;
            }

            return InteractionResult.FAIL;
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
}
