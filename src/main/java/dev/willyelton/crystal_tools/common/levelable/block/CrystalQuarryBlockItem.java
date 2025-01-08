package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryBlockItem extends LevelableBlockItem {
    public CrystalQuarryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        ItemStack stack = context.getItemInHand();
        List<BlockPos> bounds = stack.get(DataComponents.QUARRY_BOUNDS);

        if (bounds != null && bounds.size() == 4) {
            if (isOutside(bounds, context.getClickedPos()) && isOneAway(bounds, context.getClickedPos())) {
                return true;
            } else {
                Player player = context.getPlayer();
                if (player != null && !player.level().isClientSide()) {
                    player.displayClientMessage(Component.literal("\u00A7c\u00A7lQuarry must be placed next to the stabilizer boundary"), true);
                }

                return false;
            }
        }

        return super.canPlace(context, state);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof CrystalQuarryBlockEntity crystalQuarryBlockEntity) {
            List<BlockPos> positions = stack.getOrDefault(DataComponents.QUARRY_BOUNDS, new ArrayList<>());

            if (positions.size() != 4) {
                // TODO: Some default thing
                crystalQuarryBlockEntity.setPositions(BlockPos.ZERO, BlockPos.ZERO.offset(1, 1, 1));
            } else {
                crystalQuarryBlockEntity.setPositions(positions.get(0), positions.get(2).atY(-64));
            }
        }

        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    private boolean isOutside(List<BlockPos> corners, BlockPos placePosition) {
        List<Integer> xValues = corners.stream().map(BlockPos::getX).distinct().toList();
        List<Integer> zValues = corners.stream().map(BlockPos::getZ).distinct().toList();

        if (xValues.size() == 2 && zValues.size() == 2) {
            return (xValues.get(0) > placePosition.getX() && xValues.get(1) > placePosition.getX()) || (xValues.get(0) < placePosition.getX() && xValues.get(1) < placePosition.getX()) ||
                    (zValues.get(0) > placePosition.getZ() && zValues.get(1) > placePosition.getZ()) || (zValues.get(0) < placePosition.getZ() && zValues.get(1) < placePosition.getZ());
        } else {
            return false;
        }

    }

    private boolean isOneAway(List<BlockPos> corners, BlockPos placePosition) {
        for (int i = 0; i < 4; i++) {
            if (isOnLine(corners, placePosition.relative(Direction.from2DDataValue(i)))) {
                return true;
            }
        }

        return false;
    }

    private boolean isOnLine(List<BlockPos> corners, BlockPos placePosition) {
        List<Integer> xValues = corners.stream().map(BlockPos::getX).distinct().toList();
        List<Integer> zValues = corners.stream().map(BlockPos::getZ).distinct().toList();
        return (zValues.contains(placePosition.getZ()) && isInBetween(xValues, placePosition.getX())) ||
                (xValues.contains(placePosition.getX()) && isInBetween(zValues, placePosition.getZ()));
    }

    private boolean isInBetween(List<Integer> ints, int value) {
        return (ints.get(0) >= value && ints.get(1) <= value) || (ints.get(0) <= value && ints.get(1) >= value);
    }
}
