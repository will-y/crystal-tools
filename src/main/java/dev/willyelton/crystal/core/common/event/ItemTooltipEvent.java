package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.client.event.RegisterKeyBindingsEvent;
import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.LevelableStack;
import dev.willyelton.crystal.core.utils.ToolUtils;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
