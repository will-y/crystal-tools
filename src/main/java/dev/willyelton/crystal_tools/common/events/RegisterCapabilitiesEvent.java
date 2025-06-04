package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Backpack
        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, context) -> new CrystalBackpackInventory(stack),
                Registration.CRYSTAL_BACKPACK.get());

        // Furnace
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(),
                CrystalFurnaceBlockEntity::getCapForSide
        );

        // Generator
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getItemHandlerCapForSide
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getEnergyStorageCapForSide
        );

        // Quarry
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getItemHandlerCapForSide
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getEnergyStorageCapForSide
        );
    }
}
