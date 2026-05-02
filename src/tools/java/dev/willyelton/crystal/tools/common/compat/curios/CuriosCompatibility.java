package dev.willyelton.crystal.tools.common.compat.curios;

import dev.willyelton.crystal.tools.ModRegistration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static dev.willyelton.crystal.core.common.compat.curios.CuriosCompatibility.getItemInCurios;

public class CuriosCompatibility {
    public static List<ItemStack> getCrystalBackpacksInCurios(Player player) {
        return getItemInCurios(player, stack -> stack.is(ModRegistration.CRYSTAL_BACKPACK.get()));
    }
}
