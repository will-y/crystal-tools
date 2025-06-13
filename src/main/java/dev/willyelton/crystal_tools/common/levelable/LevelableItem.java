package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface LevelableItem {
    ToolMaterial CRYSTAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, CrystalToolsTags.REPAIRS_CRYSTAL);

    @Deprecated
    default int getExperienceCap(ItemStack tool) {
        return tool.getOrDefault(DataComponents.EXPERIENCE_CAP, CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
    }

    String getItemType();

    boolean isDisabled();

    default void levelableInventoryTick(ItemStack stack, Level level, Entity entity, @Nullable EquipmentSlot equipmentSlot, double repairModifier) {
        if (equipmentSlot == null || CrystalToolsConfig.REPAIR_IN_HAND.get()) {
            if (stack.getOrDefault(DataComponents.AUTO_REPAIR, 0) > 0) {
                long gameTimeToRepair = stack.getOrDefault(DataComponents.AUTO_REPAIR_GAME_TIME, -1L);
                if (gameTimeToRepair == -1L) {
                    stack.set(DataComponents.AUTO_REPAIR_GAME_TIME, level.getGameTime() + (long)(CrystalToolsConfig.TOOL_REPAIR_COOLDOWN.get() * repairModifier));
                } else if (gameTimeToRepair <= level.getGameTime()) {
                    stack.set(DataComponents.AUTO_REPAIR_GAME_TIME, level.getGameTime() + (long)(CrystalToolsConfig.TOOL_REPAIR_COOLDOWN.get() * repairModifier));
                    int repairAmount = Math.min(stack.getOrDefault(DataComponents.AUTO_REPAIR, 0), stack.getDamageValue());
                    stack.setDamageValue(stack.getDamageValue() - repairAmount);
                }
            }
        }
    }
}
