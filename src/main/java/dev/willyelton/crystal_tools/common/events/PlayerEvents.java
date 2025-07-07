package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void handleItemPickupEvent(ItemEntityPickupEvent.Pre event) {
        if (event.getItemEntity().hasPickUpDelay()) return;
        if (event.getItemEntity().getData(Registration.MAGNET_ITEM.get()) && !event.getPlayer().equals(event.getItemEntity().getOwner())) {
            List<ItemStack> magnetStacks = InventoryUtils.findAll(event.getPlayer(), stack -> stack.is(Registration.CRYSTAL_MAGNET.get()));
            magnetStacks.forEach(stack -> {
                Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, null);
                if (levelable != null) {
                    levelable.addExp(event.getPlayer().level(), event.getPlayer().getOnPos(), event.getPlayer(), 1);
                }
            });
        }

        List<ItemStack> backpackStacks = CrystalBackpack.findBackpackStacks(event.getPlayer());
        ItemStack stackToInsert = event.getItemEntity().getItem();
        ItemStack original = event.getItemEntity().getItem().copy();

        for (ItemStack backpackStack : backpackStacks) {
            if (!backpackStack.getOrDefault(DataComponents.BACKPACK_AUTO_PICKUP, false) || backpackStack.getOrDefault(DataComponents.PICKUP_DISABLED, false)) {
                continue;
            }

            CrystalBackpackInventory inventory = CrystalBackpack.getInventory(backpackStack);
            List<ItemStack> filterStacks = ToolUtils.getFilterItems(backpackStack);

            if (!ToolUtils.matchesFilter(backpackStack, stackToInsert, filterStacks)) {
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
}
