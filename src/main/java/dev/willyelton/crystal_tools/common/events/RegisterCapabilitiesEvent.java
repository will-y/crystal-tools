package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.PortableGeneratorInventory;
import dev.willyelton.crystal_tools.common.levelable.PortableGenerator;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.world.item.component.ItemContainerContents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Backpack
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

        // Portable Generator
        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, context) -> new PortableGeneratorInventory(stack),
                Registration.PORTABLE_GENERATOR.get());

        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new CrystalEnergyStorage(PortableGenerator.getCapacity(stack),
                        0, 40, PortableGenerator.getEnergy(stack)),
                Registration.PORTABLE_GENERATOR.get());

        // Furnace
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(),
                CrystalFurnaceBlockEntity::getCapForSide);

        // Generator
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getItemHandlerCapForSide);

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getEnergyStorageCapForSide);

        // Quarry
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getItemHandlerCapForSide);

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getEnergyStorageCapForSide);

        // Pedestal
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.CRYSTAL_PEDESTAL_BLOCK_ENTITY.get(),
                CrystalPedestalBlockEntity::getItemHandlerCapForSide);
    }
}
