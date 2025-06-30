package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.LevelableStack;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.LevelableTooltip;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ItemTooltipEvent {
    @SubscribeEvent
    public static void addItemToolTips(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event) {
        int index = 1;
        ItemStack stack = event.getItemStack();

        LevelableStack levelableStack = stack.getCapability(Capabilities.ITEM_SKILL, null);

        if (!ToolUtils.hasSkillTree(stack) || levelableStack == null) return;

        List<Component> tooltips = event.getToolTip();

        int durability = stack.getMaxDamage() - stack.getDamageValue();
        if (durability <= 1 && stack.getMaxDamage() != 1) {
            tooltips.add(index++, Component.literal("\u00A7c\u00A7l" + "Broken"));
        }

        int experience = stack.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0);
        int experienceCap = levelableStack.getExperienceCap();
        tooltips.add(index++, Component.literal(String.format("%d/%d XP To Next Level", experience, experienceCap)));

        int skillPoints = stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
        if (skillPoints > 0) {
            tooltips.add(index++, Component.literal(String.format("%d Unspent Skill Points (%s to open skill tree)",
                    skillPoints, RegisterKeyBindingsEvent.UPGRADE_MENU.getKey().getDisplayName().getString())));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false)
                && stack.getOrDefault(DataComponents.SILK_TOUCH_BONUS, false)
                && stack.getOrDefault(DataComponents.FORTUNE_BONUS, 0) > 0) {
            // Only show mode if it has both enchantments
            String mode = EnchantmentUtils.hasEnchantment(stack, Enchantments.SILK_TOUCH) ? "Silk Touch" : "Fortune";
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (" + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
            tooltips.add(index++, Component.literal("\u00A79" + "Mine Mode: " + mode + changeKey));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(DataComponents.HAS_3x3, false)) {
            String mode = stack.getOrDefault(DataComponents.DISABLE_3x3, false) ? "1x1" : "3x3";
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Shift + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
            tooltips.add(index++, Component.literal("\u00A79" + "Break Mode: " + mode + changeKey));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(DataComponents.AUTO_SMELT, false)) {
            boolean enabled = !stack.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false);
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Ctrl + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to toggle)";
            tooltips.add(index++, Component.literal("\u00A79" + "Auto Smelt " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        if (stack.getOrDefault(DataComponents.AUTO_TARGET, false)) {
            boolean enabled = !stack.getOrDefault(DataComponents.DISABLE_AUTO_TARGET, false);
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Shift + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to toggle)";
            tooltips.add(index++, Component.literal("\u00A79" + "Auto Target " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        int totemSlots = stack.getOrDefault(DataComponents.TOTEM_SLOTS, 0);
        if (totemSlots > 0) {
            tooltips.add(index++, Component.literal(String.format("\u00A72%d/%d Totems of Undying", stack.getOrDefault(DataComponents.FILLED_TOTEM_SLOTS, 0), totemSlots)));
        }

        LevelableTooltip levelableTooltip = stack.get(DataComponents.SKILL_TOOLTIP);
        if (levelableTooltip != null) {
            levelableTooltip.addTooltips(tooltips, event.getContext(), event.getFlags(), index);
        }
    }
}
