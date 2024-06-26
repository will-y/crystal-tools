package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillNodeType;
import dev.willyelton.crystal_tools.levelable.skill.SkillTreeRegistry;
import dev.willyelton.crystal_tools.levelable.tool.AIOLevelableTool;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ToolUtils {
    // I hate this but needed because armor needs to actually be an ArmorItem
    public static void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag, LevelableItem item) {
        if (item.isDisabled()) {
            components.add(Component.literal("\u00A7c\u00A7l" + "Disabled"));
            return;
        }
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = item.getExperienceCap(itemStack);

        int durability = item.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1 && item.getMaxDamage(itemStack) != 1) {
            components.add(Component.literal("\u00A7c\u00A7l" + "Broken"));
        }

        components.add(Component.literal(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(Component.literal(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0
                && NBTUtils.getFloatOrAddKey(itemStack, "silk_touch_bonus") > 0
                && NBTUtils.getFloatOrAddKey(itemStack, "fortune_bonus") > 0) {
            // Only show mode if it has both enchantments
            String mode = EnchantmentUtils.hasEnchantment(itemStack, Enchantments.SILK_TOUCH) ? "Silk Touch" : "Fortune";
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (" + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            components.add(Component.literal("\u00A79" + "Mine Mode: " + mode + changeKey));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0 && NBTUtils.getFloatOrAddKey(itemStack, "3x3") > 0) {
            String mode = NBTUtils.getBoolean(itemStack, "disable_3x3") ? "1x1" : "3x3";
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (Shift + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            components.add(Component.literal("\u00A79" + "Break Mode: " + mode + changeKey));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0 && NBTUtils.getFloatOrAddKey(itemStack, "auto_smelt") > 0) {
            boolean enabled = !NBTUtils.getBoolean(itemStack, "disable_auto_smelt");
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (Ctrl + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to toggle)";
            components.add(Component.literal("\u00A79" + "Auto Smelt " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        if (item instanceof AIOLevelableTool) {
            String toolTip = "\u00A79" + "Use Mode: " + StringUtils.capitalize(NBTUtils.getString(itemStack, "use_mode", "hoe").toLowerCase(Locale.ROOT));
            if (KeyBindings.modeSwitch != null) {
                toolTip = toolTip + " (alt + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            }
            components.add(Component.literal(toolTip));
        }

        if (!Screen.hasShiftDown()) {
            components.add(Component.literal("<Hold Shift For Skills>"));
        } else {
            Map<String, Float> skills = new HashMap<>();
            components.add(Component.literal("Skills:"));
            SkillData toolData = getSkillData(itemStack);
            for (SkillDataNode dataNode : toolData.getAllNodes()) {
                if (dataNode.isComplete() || (dataNode.getType().equals(SkillNodeType.INFINITE) && dataNode.getPoints() > 0)) {
                    skills.compute(dataNode.getKey(), (key, value) -> value != null ? value + dataNode.getValue() * dataNode.getPoints() : dataNode.getValue() * dataNode.getPoints());
                }
            }

            skills.forEach((s, aFloat) -> {
                components.add(Component.literal(String.format("     %s: %s", formatKey(s), formatFloat(aFloat))));
            });
        }
    }

    private static String formatFloat(float f) {
        if ((float)((int) f) == f) {
            return Integer.toString((int) f);
        } else {
            return Float.toString(f);
        }
    }

    private static String formatKey(String key) {
        return Arrays.stream(key.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        inventoryTick(itemStack, level, entity, inventorySlot, inHand, 1);
    }

    public static void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand, double modifier) {
        if (!inHand || CrystalToolsConfig.REPAIR_IN_HAND.get()) {
            if (NBTUtils.getBoolean(itemStack, "auto_repair", false)) {
                if (NBTUtils.addValueToTag(itemStack, "auto_repair_counter", 1) > CrystalToolsConfig.TOOL_REPAIR_COOLDOWN.get() * modifier) {
                    NBTUtils.setValue(itemStack, "auto_repair_counter", 0);
                    int repairAmount = Math.min((int) NBTUtils.getFloatOrAddKey(itemStack, "auto_repair"), itemStack.getDamageValue());
                    itemStack.setDamageValue(itemStack.getDamageValue() - repairAmount);
                }
            }
        }
    }

    public static boolean isBroken(ItemStack stack) {
        int durability = stack.getItem().getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");
        return durability <= 1;
    }

    public static void increaseExpCap(ItemStack stack) {
        increaseExpCap(stack, 1);
    }

    public static void increaseExpCap(ItemStack stack, int levelIncrease) {
        if (stack.getItem() instanceof LevelableItem item) {
            int experienceCap = item.getExperienceCap(stack);
            int newCap = getNewCap(experienceCap, levelIncrease);
            NBTUtils.setValue(stack, "experience_cap", newCap);
        }
    }

    public static int getNewCap(int currentCap, int levelIncrease) {
        return (int) Math.min((float) (currentCap * Math.pow(CrystalToolsConfig.EXPERIENCE_MULTIPLIER.get(), levelIncrease)), CrystalToolsConfig.MAX_EXP.get());
    }

    public static void resetPoints(ItemStack stack) {
        if (stack.hasTag()) {
            // Things to keep
            int damage = stack.getTag().getInt("Damage");
            int repairCost = stack.getTag().getInt("RepairCost");
            int skillPoints = (int) NBTUtils.getFloatOrAddKey(stack, "skill_points");

            // Total points
            int[] points = NBTUtils.getIntArray(stack, "points");

            skillPoints += Arrays.stream(points).sum();

            stack.setTag(new CompoundTag());
            stack.getTag().putInt("Damage", damage);
            stack.getTag().putInt("RepairCost", repairCost);
            NBTUtils.setValue(stack, "skill_points", (float) skillPoints);
        }
    }

    public static SkillData getSkillData(ItemStack stack) {
        int[] points = NBTUtils.getIntArray(stack, "points");
        if (stack.getItem() instanceof LevelableItem) {
            String toolType = ((LevelableItem) stack.getItem()).getItemType();
            SkillData data = SkillTreeRegistry.SKILL_TREES.get(toolType);
            data.applyPoints(points);
            return data;
        } else {
            return null;
        }
    }

    public static boolean isValidEntity(LivingEntity entity) {
        return !entity.getType().is(Registration.ENTITY_BLACKLIST);
    }
}
