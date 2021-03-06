package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillNodeType;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ToolUtils {
    // I hate this but needed because armor needs to actually be an ArmorItem
    public static void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag, LevelableItem item) {
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());

        int durability = item.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1) {
            components.add(new TextComponent("\u00A7c\u00A7l" + "Broken"));
        }

        components.add(new TextComponent(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(new TextComponent(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0
                && NBTUtils.getFloatOrAddKey(itemStack, "silk_touch_bonus") > 0
                && NBTUtils.getFloatOrAddKey(itemStack, "fortune_bonus") > 0) {
            // Only show mode if it has both enchantments
            String mode = EnchantmentUtils.hasEnchantment(itemStack, Enchantments.SILK_TOUCH) ? "Silk Touch" : "Fortune";
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (" + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            components.add(new TextComponent("\u00A79" + "Mine Mode: " + mode + changeKey));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0 && NBTUtils.getFloatOrAddKey(itemStack, "3x3") > 0) {
            String mode = NBTUtils.getBoolean(itemStack, "disable_3x3") ? "1x1" : "3x3";
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (Shift + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            components.add(new TextComponent("\u00A79" + "Break Mode: " + mode + changeKey));
        }

        if (NBTUtils.getFloatOrAddKey(itemStack, "mine_mode") > 0 && NBTUtils.getFloatOrAddKey(itemStack, "auto_smelt") > 0 && NBTUtils.getBoolean(itemStack, "disable_auto_smelt")) {
            String changeKey = KeyBindings.modeSwitch == null ? "" : " (Ctrl + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
            components.add(new TextComponent("\u00A79" + "Auto Smelt Disabled" + changeKey));
        }

//        if (!Screen.hasShiftDown()) {
//            components.add(new TextComponent("<Hold Shift For Skills>"));
//        } else {
//            components.add(new TextComponent("Skills:"));
//            int[] points = NBTUtils.getIntArray(itemStack, "points");
//            SkillData toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", item.getItemType())), points);
//            for (SkillDataNode dataNode : toolData.getAllNodes()) {
//                if (dataNode.isComplete()) {
//                    components.add(new TextComponent("    " + dataNode.getName()));
//                } else if (dataNode.getType().equals(SkillNodeType.INFINITE) && dataNode.getPoints() > 0) {
//                    components.add(new TextComponent("    " + dataNode.getName() + " (" + dataNode.getPoints() + " points)"));
//                }
//            }
//        }
    }

    public static void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        if (!inHand) {
            if (NBTUtils.getBoolean(itemStack, "auto_repair", false)) {
                if (NBTUtils.addValueToTag(itemStack, "auto_repair_counter", 1) > LevelableItem.AUTO_REPAIR_COUNTER) {
                    NBTUtils.setValue(itemStack, "auto_repair_counter", 0);
                    int repairAmount = Math.min((int) NBTUtils.getFloatOrAddKey(itemStack, "auto_repair_amount"), itemStack.getDamageValue());
                    itemStack.setDamageValue(itemStack.getDamageValue() - repairAmount);
                }
            }
        }
    }

    public static boolean isBroken(ItemStack stack) {
        int durability = stack.getItem().getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");
        return durability <= 1;
    }
}
