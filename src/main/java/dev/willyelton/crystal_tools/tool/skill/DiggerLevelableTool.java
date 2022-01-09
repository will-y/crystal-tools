package dev.willyelton.crystal_tools.tool.skill;

import dev.willyelton.crystal_tools.tool.LevelableTool;
import dev.willyelton.crystal_tools.utils.LevelUtilities;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Class for the shovel and pickaxe
 * Handles the 3x3 mining
 */
public class DiggerLevelableTool extends LevelableTool {
    public DiggerLevelableTool(Properties properties, Tag<Block> mineableBlocks, String toolType) {
        super(properties, mineableBlocks, toolType);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            tool.hurtAndBreak(1, entity, (player) -> {
                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        if (NBTUtils.getFloatOrAddKey(tool, "3x3") > 0) {
            // TODO: make this actually what I want not this
            Direction direction = Direction.fromYRot(entity.getYRot());
            if (entity.getXRot() < -45.0F) {
                direction = Direction.UP;
            } else if (entity.getXRot() > 45.0F) {
                direction = Direction.DOWN;
            }
            blockBreakerHelper(tool, level, blockPos, entity, direction);
        }



        addExp(tool, level, blockPos);

        return true;
    }

    // just going to hard code it for now
    private void blockBreakerHelper(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity, Direction blockFace) {
        switch (blockFace) {
            case UP, DOWN -> {
                // x-z plane break
                breakBlock(tool, level, blockPos.north(), entity);
                breakBlock(tool, level, blockPos.south(), entity);
                breakBlock(tool, level, blockPos.east(), entity);
                breakBlock(tool, level, blockPos.west(), entity);
                breakBlock(tool, level, blockPos.north().east(), entity);
                breakBlock(tool, level, blockPos.north().west(), entity);
                breakBlock(tool, level, blockPos.south().east(), entity);
                breakBlock(tool, level, blockPos.south().west(), entity);
            }
            case NORTH, SOUTH -> {
                // x-y or z-y
                breakBlock(tool, level, blockPos.above(), entity);
                breakBlock(tool, level, blockPos.below(), entity);
                breakBlock(tool, level, blockPos.east(), entity);
                breakBlock(tool, level, blockPos.west(), entity);
                breakBlock(tool, level, blockPos.east().above(), entity);
                breakBlock(tool, level, blockPos.west().above(), entity);
                breakBlock(tool, level, blockPos.east().below(), entity);
                breakBlock(tool, level, blockPos.west().below(), entity);
            }
            case EAST, WEST -> {
                // x-y or z-y
                breakBlock(tool, level, blockPos.above(), entity);
                breakBlock(tool, level, blockPos.below(), entity);
                breakBlock(tool, level, blockPos.north(), entity);
                breakBlock(tool, level, blockPos.south(), entity);
                breakBlock(tool, level, blockPos.north().above(), entity);
                breakBlock(tool, level, blockPos.south().above(), entity);
                breakBlock(tool, level, blockPos.north().below(), entity);
                breakBlock(tool, level, blockPos.south().below(), entity);
            }
        }
    }

    private void breakBlock(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity) {
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(tool, blockState)) {
            LevelUtilities.destroyBlock(level, blockPos, true, entity, 512, tool);
//            level.destroyBlock(blockPos, true, entity);
            if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
                tool.hurtAndBreak(1, entity, (player) -> {
                    player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }
            addExp(tool, level, blockPos);
        }
    }
}
