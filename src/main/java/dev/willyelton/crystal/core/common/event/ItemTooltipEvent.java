package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.client.event.RegisterKeyBindingsEvent;
import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.LevelableStack;
import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.utils.EnchantmentUtils;
import dev.willyelton.crystal.core.utils.ToolUtils;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class ItemTooltipEvent {
    @SubscribeEvent
    public static void addItemToolTips(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event) {
        int index = 1;
        ItemStack stack = event.getItemStack();

        LevelableStack levelableStack = stack.getCapability(Capabilities.ITEM_SKILL, null);

        if (!ToolUtils.hasSkillTree(stack) || levelableStack == null) return;

        List<Component> tooltips = event.getToolTip();
        int durability = stack.getMaxDamage() - stack.getDamageValue();
        if (durability <= 1 && stack.getMaxDamage() != 1 && stack.getDamageValue() != 0) {
            tooltips.add(index++, Component.literal("\u00A7c\u00A7l" + "Broken"));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false)
                && stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.SILK_TOUCH_BONUS, false)
                && stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.FORTUNE_BONUS, 0) > 0) {
            // Only show mode if it has both enchantments
            String mode = EnchantmentUtils.hasEnchantment(stack, Enchantments.SILK_TOUCH) ? "Silk Touch" : "Fortune";
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (" + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to change)";
            tooltips.add(index++, Component.literal("\u00A79" + "Mine Mode: " + mode + changeKey));
        }

        if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.AUTO_SMELT, false)) {
            boolean enabled = !stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.DISABLE_AUTO_SMELT, false);
            String changeKey = RegisterKeyBindingsEvent.MODE_SWITCH == null ? "" : " (Ctrl + " + RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString() + " to toggle)";
            tooltips.add(index++, Component.literal("\u00A79" + "Auto Smelt " + (enabled ? "Enabled" : "Disabled") + changeKey));
        }

        int experience = stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.SKILL_EXPERIENCE, 0);
        int experienceCap = levelableStack.getExperienceCap();
        tooltips.add(index++, Component.literal(String.format("%d/%d XP To Next Level", experience, experienceCap)));

        int skillPoints = stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.SKILL_POINTS, 0);
        if (skillPoints > 0) {
            tooltips.add(index++, Component.literal(String.format("%d Unspent Skill Point(s) (%s to open skill tree)",
                    skillPoints, RegisterKeyBindingsEvent.UPGRADE_MENU.getKey().getDisplayName().getString())));
        }
    }
}
