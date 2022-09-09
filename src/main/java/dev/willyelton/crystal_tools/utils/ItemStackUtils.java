package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.levelable.LevelableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemStackUtils {
    public static ItemStack getHeldLevelableTool(Player player) {
        for (ItemStack i : player.getHandSlots()) {
            if (i.getItem() instanceof LevelableItem) {
                return i;
            }
        }

        return ItemStack.EMPTY;
    }

}
