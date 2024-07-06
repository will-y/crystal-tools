package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void handleItemPickupEvent(ItemEntityPickupEvent.Pre event) {
        if (event.getItemEntity().hasPickUpDelay()) return;
        List<ItemStack> backpackStacks = CrystalBackpack.findBackpackStacks(event.getPlayer());
        ItemStack stackToInsert = event.getItemEntity().getItem();
        ItemStack original = event.getItemEntity().getItem().copy();

        for (ItemStack backpackStack : backpackStacks) {
            if (!backpackStack.getOrDefault(DataComponents.BACKPACK_AUTO_PICKUP, false) || backpackStack.getOrDefault(DataComponents.PICKUP_DISABLED, false)) {
                continue;
            }

            CrystalBackpackInventory inventory = CrystalBackpack.getInventory(backpackStack);
            List<ItemStack> filterStacks = CrystalBackpack.getFilterItems(backpackStack);

            if (!shouldPickup(backpackStack, stackToInsert, filterStacks)) {
                continue;
            }

            stackToInsert = inventory.insertStack(stackToInsert);

            if (stackToInsert.isEmpty()) {
                event.getItemEntity().getItem().setCount(stackToInsert.getCount());
                event.setCanPickup(TriState.TRUE);
                return;
            }
        }

        if (original.getCount() == stackToInsert.getCount()) {
            event.setCanPickup(TriState.DEFAULT);
//            event.setResult(Event.Result.DEFAULT);
        } else {
            // TODO: this is a little janky, waits for next pickup to do this
            event.getItemEntity().setItem(stackToInsert);
            event.setCanPickup(TriState.DEFAULT);
//            event.setCanceled(true);
        }
    }

    private static boolean shouldPickup(ItemStack backpackStack, ItemStack pickupStack, List<ItemStack> filter) {
        // TODO: matching modes (respect nbt)
        // If there are no filters, default to pickup
        if (backpackStack.getOrDefault(DataComponents.FILTER_CAPACITY, 0) == 0) {
            return true;
        }
        boolean whiteList = backpackStack.getOrDefault(DataComponents.WHITELIST, true);
        for (ItemStack filterStack : filter) {
            if (filterStack.is(pickupStack.getItem())) {
                return whiteList;
            }
        }

        return !whiteList;
    }
}
