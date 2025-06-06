package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.StringUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface LevelableItem {
   ToolMaterial CRYSTAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, CrystalToolsTags.REPAIRS_CRYSTAL);

    default void addExp(ItemStack tool, Level level, BlockPos blockPos, LivingEntity player) {
        addExp(tool, level, blockPos, player, 1);
    }

    default void addExp(ItemStack tool, Level level, BlockPos blockPos,LivingEntity livingEntity, int amount) {
        int newExperience = DataComponents.addToComponent(tool, DataComponents.SKILL_EXPERIENCE, amount);
        int experienceCap = getExperienceCap(tool);

        if (newExperience >= experienceCap) {
            // duration up
            DataComponents.addToComponent(tool, DataComponents.SKILL_POINTS, 1);
            // play duration up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            if (livingEntity instanceof Player player) {
                if (tool.getItem() instanceof LevelableItem item) {
                    player.displayClientMessage(Component.literal("\u00A7b" + tool.getItem().getName(tool).getString() + " Leveled Up (" + item.getSkillPoints(tool) + " Unspent Points)"), true);
                }
            }
            tool.set(DataComponents.SKILL_EXPERIENCE, Math.max(0, newExperience - experienceCap));
            ToolUtils.increaseExpCap(tool);
        }
    }

    default int getSkillPoints(ItemStack stack) {
        return stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    default int getExperienceCap(ItemStack tool) {
        return tool.getOrDefault(DataComponents.EXPERIENCE_CAP, CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
    }

    String getItemType();

    boolean isDisabled();

    default void appendLevelableHoverText(ItemStack stack, Consumer<Component> components, LevelableItem item, TooltipFlag tooltipFlag, Item.TooltipContext context) {
        if (item.isDisabled()) {
            components.accept(Component.literal("\u00A7c\u00A7l" + "Disabled"));
            return;
        }
        int newExperience = stack.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0);
        int experienceCap = item.getExperienceCap(stack);

        int durability = stack.getMaxDamage() - stack.getDamageValue();

        if (durability <= 1 && stack.getMaxDamage() != 1) {
            components.accept(Component.literal("\u00A7c\u00A7l" + "Broken"));
        }

        components.accept(Component.literal(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
        if (skillPoints > 0) {
            components.accept(Component.literal(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false)
                && stack.getOrDefault(DataComponents.SILK_TOUCH_BONUS, false)
                && stack.getOrDefault(DataComponents.FORTUNE_BONUS, 0) > 0) {
            // Only show mode if it has both enchantments
            String mode = EnchantmentUtils.hasEnchantment(stack, Enchantments.SILK_TOUCH) ? "Silk Touch" : "Fortune";
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (" + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
            components.accept(Component.literal("\u00A79" + "Mine Mode: " + mode + changeKey));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(DataComponents.HAS_3x3, false)) {
            String mode = stack.getOrDefault(DataComponents.DISABLE_3x3, false) ? "1x1" : "3x3";
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Shift + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
            components.accept(Component.literal("\u00A79" + "Break Mode: " + mode + changeKey));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(DataComponents.AUTO_SMELT, false)) {
            boolean enabled = !stack.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false);
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Ctrl + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to toggle)";
            components.accept(Component.literal("\u00A79" + "Auto Smelt " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        if (stack.getOrDefault(DataComponents.AUTO_TARGET, false)) {
            boolean enabled = !stack.getOrDefault(DataComponents.DISABLE_AUTO_TARGET, false);
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Shift + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to toggle)";
            components.accept(Component.literal("\u00A79" + "Auto Target " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        addAdditionalTooltips(stack, components, item);

        if (!tooltipFlag.hasShiftDown()) {
            components.accept(Component.literal("<Hold Shift For Skills>"));
        } else {
            Map<String, Float> skills = new HashMap<>();
            components.accept(Component.literal("\u00A76Skills:"));
            Optional<Holder.Reference<SkillData>> toolData = ToolUtils.getSkillData(context.level(), stack);
            SkillPoints points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());

            if (toolData.isPresent()) {
                SkillData skillData = toolData.get().value();
                for (SkillDataNode dataNode : skillData.getAllNodes()) {
                    int nodePoints = points.getPoints(dataNode.getId());
                    if (nodePoints > 0) {
                        skills.compute(StringUtils.stripRomanNumeral(dataNode.getName()), (key, value) -> value != null ? value + nodePoints : nodePoints);
                    }
                }

                skills.forEach((s, aFloat) -> {
                    components.accept(Component.literal(String.format("\u00A76     %s: %s", StringUtils.formatKey(s), StringUtils.formatFloat(aFloat))));
                });
            }
        }
    }

    default void addAdditionalTooltips(ItemStack stack, Consumer<Component> components, LevelableItem item) {}

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
