package dev.willyelton.crystal.core.common.levelable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemClickHandler {
    /// Called when a player left-clicks a block with this item.
    ///
    /// Return true if you want to cancel the block breaking event
    default boolean leftClickBlock(ItemStack stack, Level level, Player player, BlockPos pos, Direction face) {
        return false;
    }

    default void leftClickAir(ItemStack stack, Level level, Player player, boolean shiftDown) {}
}
