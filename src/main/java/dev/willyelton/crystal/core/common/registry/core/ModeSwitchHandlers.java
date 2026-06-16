package dev.willyelton.crystal.core.common.registry.core;

import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.common.registry.ModeSwitchHandlerRegistry;
import dev.willyelton.crystal.core.utils.EnchantmentUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantments;

public class ModeSwitchHandlers {
    private ModeSwitchHandlers() {}

    public static void registerModeSwitchHandlers() {
        // Auto Smelt
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (stack.getOrDefault(DataComponents.MINE_MODE, false) && stack.getOrDefault(DataComponents.AUTO_SMELT, false)
                    && hasCtrlDown && !hasShiftDown && !hasAltDown) {
                boolean disableAutoSmelt = stack.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false);
                stack.set(DataComponents.DISABLE_AUTO_SMELT, !disableAutoSmelt);
                player.sendOverlayMessage(Component.literal("Auto Smelt " + (!disableAutoSmelt ? "Disabled" : "Enabled")));
                return true;
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);

        // Fortune / Silk Touch
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (stack.getOrDefault(DataComponents.MINE_MODE, false) && !hasShiftDown && !hasCtrlDown && !hasAltDown) {
                if (EnchantmentUtils.hasEnchantment(stack, Enchantments.SILK_TOUCH) && stack.getOrDefault(DataComponents.FORTUNE_BONUS, 0) > 0) {
                    EnchantmentUtils.removeEnchantment(stack, Enchantments.SILK_TOUCH);
                    EnchantmentUtils.addEnchantment(stack, Enchantments.FORTUNE, stack.getOrDefault(DataComponents.FORTUNE_BONUS, 3), player);
                    player.sendOverlayMessage(Component.literal("Mine Mode: Fortune"));
                    return true;
                } else if (EnchantmentUtils.hasEnchantment(stack, Enchantments.FORTUNE) && stack.getOrDefault(DataComponents.SILK_TOUCH_BONUS, false)) {
                    EnchantmentUtils.removeEnchantment(stack, Enchantments.FORTUNE);
                    EnchantmentUtils.addEnchantment(stack, Enchantments.SILK_TOUCH, 1, player);
                    player.sendOverlayMessage(Component.literal("Mine Mode: Silk Touch"));
                    return true;
                }
            }

            return false;
        });
    }
}
