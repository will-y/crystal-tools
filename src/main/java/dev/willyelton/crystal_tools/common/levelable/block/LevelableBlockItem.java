package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.FurnaceUpgrades;
import dev.willyelton.crystal_tools.common.components.GeneratorData;
import dev.willyelton.crystal_tools.common.components.GeneratorUpgrades;
import dev.willyelton.crystal_tools.common.components.LevelableBlockEntityData;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LevelableBlockItem extends BlockItem {
    public LevelableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        LevelableBlockEntityData entityData = stack.get(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA);
        if (entityData != null) {
            components.add(Component.literal(String.format("%d/%d XP To Next Level", entityData.exp(), entityData.expCap())));
            int skillPoints = entityData.skillPoints();
            if (skillPoints > 0) {
                components.add(Component.literal(String.format("%d Unspent Skill Points", skillPoints)));
            }
        }

        GeneratorData generatorData = stack.get(DataComponents.GENERATOR_DATA);
        if (generatorData != null) {
            components.add(Component.literal(String.format("%d FE Stored", generatorData.energy())));
        }

        if (!tooltipFlag.hasShiftDown()) {
            components.add(Component.literal("<Hold Shift For Skills>"));
        } else {
            FurnaceUpgrades furnaceUpgrades = stack.get(DataComponents.FURNACE_UPGRADES);
            if (furnaceUpgrades != null) {
                if (furnaceUpgrades.speed() > 0) {
                    components.add(Component.literal(String.format("     %s: %s", "Speed", StringUtils.formatFloat(furnaceUpgrades.speed()))));
                }
                if (furnaceUpgrades.fuelEfficiency() > 0) {
                    components.add(Component.literal(String.format("     %s: %d", "Fuel Efficiency", furnaceUpgrades.fuelEfficiency())));
                }
                if (furnaceUpgrades.slots() > 0) {
                    components.add(Component.literal(String.format("     %s: %d", "Extra Slots", furnaceUpgrades.slots())));
                }
                if (furnaceUpgrades.fuelEfficiency() > 0) {
                    components.add(Component.literal(String.format("     %s: %d", "Extra Fuel Slots", furnaceUpgrades.fuelSlots())));
                }
                if (furnaceUpgrades.balance()) {
                    components.add(Component.literal("     Auto Balance"));
                }
                // TODO: actions
//                if (furnaceUpgrades.autoOutput()) {
//                    components.add(Component.literal("     Auto Output"));
//                }
                if (furnaceUpgrades.saveFuel()) {
                    components.add(Component.literal("     Save Fuel"));
                }
            }

            GeneratorUpgrades generatorUpgrades = stack.get(DataComponents.GENERATOR_UPGRADES);
            if (generatorUpgrades != null) {
                if (generatorUpgrades.feGeneration() > 0) {
                    components.add(Component.literal(String.format("     %s: %d", "Bonus Generation", generatorUpgrades.feGeneration())));
                }
                if (generatorUpgrades.feStorage() > 0) {
                    components.add(Component.literal(String.format("     %s: %d", "Bonus Capacity", generatorUpgrades.feStorage())));
                }
                if (generatorUpgrades.fuelEfficiency() > 0) {
                    components.add(Component.literal(String.format("     %s: %s%%", "Fuel Efficiency", StringUtils.formatPercent(generatorUpgrades.fuelEfficiency()))));
                }
                if (generatorUpgrades.redstoneControl()) {
                    components.add(Component.literal("     Redstone Control"));
                }
                if (generatorUpgrades.saveFuel()) {
                    components.add(Component.literal("     Save Fuel"));
                }
                if (generatorUpgrades.metalGenerator()) {
                    components.add(Component.literal("     Metal Generator"));
                }
                if (generatorUpgrades.foodGenerator()) {
                    components.add(Component.literal("     Food Generator"));
                }
                if (generatorUpgrades.gemGenerator()) {
                    components.add(Component.literal("     Gem Generator"));
                }
            }
        }

        ItemContainerContents container = getContainer(stack);
        if (container != null && !container.stream().allMatch(ItemStack::isEmpty)) {
            if (!tooltipFlag.hasAltDown()) {
                components.add(Component.literal("<Hold Alt For Stored Items>"));
            } else {
                container.stream().forEach(storedStack -> {
                    if (!storedStack.isEmpty()) {
                        components.add(Component.literal(ItemStackUtils.toString(storedStack)));
                    }
                });
            }
        }
    }

    private @Nullable ItemContainerContents getContainer(ItemStack stack) {
        ItemContainerContents container = stack.get(DataComponents.FURNACE_INVENTORY);

        if (container == null) {
            container = stack.get(net.minecraft.core.component.DataComponents.CONTAINER);
        }

        return container;
    }
}
