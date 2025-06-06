package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemStackUtils {
    public static ItemStack getHeldLevelableTool(Player player) {
        for (ItemStack i : InventoryUtils.getHandItems(player)) {
            Optional<Holder.Reference<SkillData>> optionalReference = ToolUtils.getSkillData(player.level(), i);
            if (optionalReference.isPresent()) {
                return i;
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean sameItem(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.is(itemStack2.getItem());
    }

    public static String toString(ItemStack stack) {
        if (stack.isEmpty()) {
            return "Empty Stack";
        }

        return String.format("%d x %s", stack.getCount(), stack.getItem());
    }
}
