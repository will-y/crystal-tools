package dev.willyelton.crystal_tools.compat.curios;

import dev.willyelton.crystal_tools.Registration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuriosCompatibility {
    public static List<ItemStack> getCrystalBackpacksInCurios(Player player) {
        if (!ModList.get().isLoaded("curios")) {
            return Collections.emptyList();
        }

        List<ItemStack> toReturn = new ArrayList<>();

        LazyOptional<ICuriosItemHandler> optionalInventory = CuriosApi.getCuriosInventory(player);

        if (optionalInventory.isPresent() && optionalInventory.resolve().isPresent()) {
            ICuriosItemHandler curiosInventory = optionalInventory.resolve().get();
            curiosInventory.getCurios().forEach((identifier, curioStacksHandler) -> {
                IDynamicStackHandler handler = curioStacksHandler.getStacks();
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);

                        if (stack.is(Registration.CRYSTAL_BACKPACK.get())) {
                            toReturn.add(stack);
                        }
                    }
                }
            });
        }

        return toReturn;
    }
}
