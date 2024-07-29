package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, context) -> {
                    // TODO (breaking) remove this
                    if (stack.has(DataComponents.INVENTORY_OLD)) {
                        ItemContainerContents contents = stack.get(DataComponents.INVENTORY_OLD);
                        if (contents != null) {
                            stack.remove(DataComponents.INVENTORY_OLD);
                            stack.set(DataComponents.BACKPACK_INVENTORY, List.of(contents));
                        }
                    }
                    return new CrystalBackpackInventory(stack);
                },
                Registration.CRYSTAL_BACKPACK.get());

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(),
                CrystalFurnaceBlockEntity::getCapForSide
        );
    }
}
