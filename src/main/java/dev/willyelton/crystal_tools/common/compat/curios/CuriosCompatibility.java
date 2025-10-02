package dev.willyelton.crystal_tools.common.compat.curios;

import dev.willyelton.crystal_tools.ModRegistration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompatibility {
    public static List<ItemStack> getCrystalBackpacksInCurios(Player player) {
        return getItemInCurios(player, stack -> stack.is(ModRegistration.CRYSTAL_BACKPACK.get()));
    }

    public static List<ItemStack> getItemInCurios(Player player, Predicate<ItemStack> predicate) {
        if (!ModList.get().isLoaded("curios")) {
            return new ArrayList<>();
        }

        List<ItemStack> toReturn = new ArrayList<>();
        Optional<ICuriosItemHandler> optionalInventory = CuriosApi.getCuriosInventory(player);

        optionalInventory.ifPresent(curiosInventory -> curiosInventory.getCurios().forEach((identifier, curioStacksHandler) -> {
            IDynamicStackHandler handler = curioStacksHandler.getStacks();
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);

                    if (predicate.test(stack)) {
                        toReturn.add(stack);
                    }
                }
            }
        }));

        return toReturn;
    }
}
