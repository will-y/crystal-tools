package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.block.CrystalTorch;
import dev.willyelton.crystal_tools.block.CrystalWallTorch;
import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.item.tool.LevelableTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class ToolUseUtils {
    public static InteractionResult useOnTorch(UseOnContext context, LevelableTool tool) {
        ItemStack itemStack = context.getItemInHand();
        if (NBTUtils.getFloatOrAddKey(itemStack, "torch") > 0) {
            Level level = context.getLevel();
            BlockPos position = context.getClickedPos();
            BlockState state = level.getBlockState(position);
            Direction direction = context.getClickedFace();
            position = position.relative(direction);
            BlockState torchBlockState;

            if (direction.equals(Direction.UP)) {
                torchBlockState = ModBlocks.CRYSTAL_TORCH.get().defaultBlockState().setValue(CrystalTorch.DROP_ITEM, false);
            } else if (direction.equals(Direction.DOWN)) {
                return InteractionResult.PASS;
            } else {
                torchBlockState = ModBlocks.CRYSTAL_WALL_TORCH.get().defaultBlockState().setValue(CrystalWallTorch.FACING, direction).setValue(CrystalTorch.DROP_ITEM, false);
            }

            if (level.getBlockState(position).is(Blocks.AIR) && state.isFaceSturdy(level, position, direction)) {
                level.setBlock(position, torchBlockState, 0);
            } else {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide && context.getPlayer() != null) {
                itemStack.hurtAndBreak(10, context.getPlayer(), (player) -> {
                    player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }
            tool.addExp(itemStack, level, context.getClickedPos(), context.getPlayer());
        }

        return InteractionResult.PASS;
    }
}
