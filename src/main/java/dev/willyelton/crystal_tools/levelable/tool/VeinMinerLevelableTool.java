package dev.willyelton.crystal_tools.levelable.tool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public interface VeinMinerLevelableTool {
    Predicate<BlockState> getVeinMinerPredicate(BlockState minedBlockState);

    int getMaxBlocks(ItemStack stack);

    boolean canVeinMin(BlockState blockState);
}
