package dev.willyelton.crystal.tools.common.levelable.block;

import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.components.FurnaceUpgrades;
import dev.willyelton.crystal.tools.common.components.GeneratorData;
import dev.willyelton.crystal.tools.common.components.GeneratorUpgrades;
import dev.willyelton.crystal.core.common.datacomponent.LevelableBlockEntityData;
import dev.willyelton.crystal.core.utils.ItemStackUtils;
import dev.willyelton.crystal.core.utils.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LevelableBlockItem extends BlockItem {
    public LevelableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        LevelableBlockEntityData entityData = stack.get(dev.willyelton.crystal.core.common.datacomponent.DataComponents.LEVELABLE_BLOCK_ENTITY_DATA);
        if (entityData != null) {
            components.accept(Component.literal(String.format("%d/%d XP To Next Level", entityData.exp(), entityData.expCap())));
            int skillPoints = entityData.skillPoints();
            if (skillPoints > 0) {
                components.accept(Component.literal(String.format("%d Unspent Skill Points", skillPoints)));
            }
        }

        GeneratorData generatorData = stack.get(DataComponents.GENERATOR_DATA);
        if (generatorData != null) {
            components.accept(Component.literal(String.format("%d FE Stored", generatorData.energy())));
        }

        if (!tooltipFlag.hasShiftDown()) {
            components.accept(Component.literal("<Hold Shift For Skills>"));
        } else {
            components.accept(Component.literal("\u00A76Skills:"));
            FurnaceUpgrades furnaceUpgrades = stack.get(DataComponents.FURNACE_UPGRADES);
            if (furnaceUpgrades != null) {
                if (furnaceUpgrades.speed() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %s", "Speed", StringUtils.formatFloat(furnaceUpgrades.speed()))));
                }
                if (furnaceUpgrades.fuelEfficiency() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %d", "Fuel Efficiency", furnaceUpgrades.fuelEfficiency())));
                }
                if (furnaceUpgrades.slots() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %d", "Extra Slots", furnaceUpgrades.slots())));
                }
                if (furnaceUpgrades.fuelEfficiency() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %d", "Extra Fuel Slots", furnaceUpgrades.fuelSlots())));
                }
                if (furnaceUpgrades.balance()) {
                    components.accept(Component.literal("\u00A76     Auto Balance"));
                }
                if (furnaceUpgrades.saveFuel()) {
                    components.accept(Component.literal("\u00A76     Save Fuel"));
                }
            }

            GeneratorUpgrades generatorUpgrades = stack.get(DataComponents.GENERATOR_UPGRADES);
            if (generatorUpgrades != null) {
                if (generatorUpgrades.feGeneration() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %d", "Bonus Generation", generatorUpgrades.feGeneration())));
                }
                if (generatorUpgrades.feStorage() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %d", "Bonus Capacity", generatorUpgrades.feStorage())));
                }
                if (generatorUpgrades.fuelEfficiency() > 0) {
                    components.accept(Component.literal(String.format("\u00A76     %s: %s%%", "Fuel Efficiency", StringUtils.formatPercent(generatorUpgrades.fuelEfficiency()))));
                }
                if (generatorUpgrades.redstoneControl()) {
                    components.accept(Component.literal("\u00A76     Redstone Control"));
                }
                if (generatorUpgrades.saveFuel()) {
                    components.accept(Component.literal("\u00A76     Save Fuel"));
                }
                if (generatorUpgrades.metalGenerator()) {
                    components.accept(Component.literal("\u00A76     Metal Generator"));
                }
                if (generatorUpgrades.foodGenerator()) {
                    components.accept(Component.literal("\u00A76     Food Generator"));
                }
                if (generatorUpgrades.gemGenerator()) {
                    components.accept(Component.literal("\u00A76     Gem Generator"));
                }
            }

            // Actions
            if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.AUTO_OUTPUT, false)) {
                components.accept(Component.literal("\u00A76     Auto Output"));
            }
            if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.CHUNKLOADING, false)) {
                components.accept(Component.literal("\u00A76     Chunkloading"));
            }
        }

        ItemContainerContents container = getContainer(stack);
        if (container != null && !container.allItemsCopyStream().allMatch(ItemStack::isEmpty)) {
            if (!tooltipFlag.hasAltDown()) {
                components.accept(Component.literal("<Hold Alt For Stored Items>"));
            } else {
                container.allItemsCopyStream().forEach(storedStack -> {
                    if (!storedStack.isEmpty()) {
                        components.accept(Component.literal(ItemStackUtils.toString(storedStack)));
                    }
                });
            }
        }
    }

    private @Nullable ItemContainerContents getContainer(ItemStack stack) {
        return stack.get(net.minecraft.core.component.DataComponents.CONTAINER);
    }
}
