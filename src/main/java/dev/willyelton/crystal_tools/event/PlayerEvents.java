package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void handleItemPickupEvent(EntityItemPickupEvent event) {
        List<ItemStack> backpackStacks = CrystalBackpack.findBackpackStacks(event.getEntity());
        ItemStack stackToInsert = event.getItem().getItem();
        ItemStack original = event.getItem().getItem().copy();

        for (ItemStack backpackStack : backpackStacks) {
            CrystalBackpackInventory inventory = CrystalBackpack.getInventory(backpackStack);
            List<ItemStack> filterStacks = CrystalBackpack.getFilterItems(backpackStack);

            if (!shouldPickup(stackToInsert, filterStacks)) {
                continue;
            }

            stackToInsert = inventory.insertStack(stackToInsert);

            if (stackToInsert.isEmpty()) {
                event.getItem().setItem(stackToInsert);
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }

        if (original.getCount() == stackToInsert.getCount()) {
            event.setResult(Event.Result.DEFAULT);
        } else {
            // TODO: this is a little janky, waits for next pickup to do this
            event.getItem().setItem(stackToInsert);
            event.setCanceled(true);
        }
    }

    private static boolean shouldPickup(ItemStack stack, List<ItemStack> filter) {
        // TODO: matching modes (respect nbt)
        boolean whiteList = NBTUtils.getBoolean(stack, "white_list");
        for (ItemStack filterStack : filter) {
            if (filterStack.is(stack.getItem())) {
                return whiteList;
            }
        }

        return !whiteList;
    }
}
