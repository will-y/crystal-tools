package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
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
            // TODO: filters
            CrystalBackpackInventory inventory = CrystalBackpack.getInventory(backpackStack);
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
}
