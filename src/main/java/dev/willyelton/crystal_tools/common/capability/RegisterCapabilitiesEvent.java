package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.energy.CrystalEnergyStorage;
import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import dev.willyelton.crystal_tools.common.inventory.PortableGeneratorInventory;
import dev.willyelton.crystal_tools.common.levelable.PortableGenerator;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Backpack
        event.registerItem(Capabilities.Item.ITEM,
                (stack, context) -> new CrystalBackpackInventory(stack),
                ModRegistration.CRYSTAL_BACKPACK.get());

        // Portable Generator
        event.registerItem(Capabilities.Item.ITEM,
                (stack, context) -> new PortableGeneratorInventory(stack),
                ModRegistration.PORTABLE_GENERATOR.get());

        event.registerItem(Capabilities.Energy.ITEM,
                (stack, context) -> new CrystalEnergyStorage(PortableGenerator.getCapacity(stack),
                        0, 40, PortableGenerator.getEnergy(stack)),
                ModRegistration.PORTABLE_GENERATOR.get());

        // Furnace
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModRegistration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(),
                CrystalFurnaceBlockEntity::getCapForSide);

        // Generator
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModRegistration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getItemHandlerCapForSide);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModRegistration.CRYSTAL_GENERATOR_BLOCK_ENTITY.get(),
                CrystalGeneratorBlockEntity::getEnergyStorageCapForSide);

        // Quarry
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModRegistration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getItemHandlerCapForSide);

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModRegistration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(),
                CrystalQuarryBlockEntity::getEnergyStorageCapForSide);

        // Pedestal
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModRegistration.CRYSTAL_PEDESTAL_BLOCK_ENTITY.get(),
                CrystalPedestalBlockEntity::getItemHandlerCapForSide);

        // All levelable items
        for (Item item : BuiltInRegistries.ITEM) {
            event.registerItem(dev.willyelton.crystal_tools.common.capability.Capabilities.ITEM_SKILL,
                    LevelableStack::of,
                    item);
        }
    }
}
