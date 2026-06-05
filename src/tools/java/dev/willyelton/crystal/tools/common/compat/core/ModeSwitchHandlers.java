package dev.willyelton.crystal.tools.common.compat.core;

import dev.willyelton.crystal.core.common.registry.ModeSwitchHandlerRegistry;
import dev.willyelton.crystal.core.common.skill.SkillData;
import dev.willyelton.crystal.core.utils.AttributeUtils;
import dev.willyelton.crystal.core.utils.EnchantmentUtils;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.levelable.tool.UseMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.NeoForgeMod;

public class ModeSwitchHandlers {
    private ModeSwitchHandlers() {}

    public static void registerModeSwitchHandlers() {
        // 3x3 Mining
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.MINE_MODE, false) && hasShiftDown && !hasCtrlDown && !hasAltDown) {
                // 3x3 or 1x1 mode
                if (stack.getOrDefault(DataComponents.HAS_3x3, false)) {
                    boolean disable3x3 = stack.getOrDefault(DataComponents.DISABLE_3x3, false);
                    stack.set(DataComponents.DISABLE_3x3, !disable3x3);
                    player.sendOverlayMessage(Component.literal("Break Mode: " + (!disable3x3 ? "1x1" : "3x3")));
                    return true;
                }
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);

        // Armor
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (isHeld) {
                return disableFrostWalker(player, stack) || disableNightVision(player, stack) || disableCreativeFlight(player, stack, skillData);
            }

            if (hasShiftDown) {
                return disableFrostWalker(player, stack);
            } else if (hasCtrlDown) {
                return disableCreativeFlight(player, stack, skillData);
            } else {
                return disableNightVision(player, stack);
            }
        }, ModeSwitchHandlerRegistry.HIGHEST);

        // Backpack
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            // TODO: Does this display messages when you don't have the pickup upgrade yet?
            if (stack.is(ModRegistration.CRYSTAL_BACKPACK)) {
                boolean pickupDisabled = stack.getOrDefault(DataComponents.PICKUP_DISABLED, false);
                stack.set(DataComponents.PICKUP_DISABLED, !pickupDisabled);
                player.sendOverlayMessage(Component.literal("Auto Pickup " + (pickupDisabled ? "Enabled" : "Disabled")));

                return true;
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);

        // AIOT
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (hasAltDown && !hasShiftDown && !hasCtrlDown && stack.is(ModRegistration.CRYSTAL_AIOT)) {
                UseMode currentMode = stack.getOrDefault(DataComponents.USE_MODE, UseMode.HOE);
                stack.set(DataComponents.USE_MODE, UseMode.nextMode(stack, currentMode));
                player.sendOverlayMessage(Component.literal("Mode: " + UseMode.nextMode(stack, currentMode)));
                return true;
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);

        // Trident
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.MINE_MODE, false) && stack.is(ModRegistration.CRYSTAL_TRIDENT)) {
                boolean riptideDisabled = stack.getOrDefault(DataComponents.RIPTIDE_DISABLED, false);
                stack.set(DataComponents.RIPTIDE_DISABLED, !riptideDisabled);
                player.sendOverlayMessage(Component.literal("Riptide " + (riptideDisabled ? "Enabled" : "Disabled")));
                return true;
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);

        // Auto Target
        ModeSwitchHandlerRegistry.addModeSwitchHandler((player, stack, skillData, hasShiftDown, hasCtrlDown, hasAltDown, isHeld) -> {
            if (stack.getOrDefault(DataComponents.AUTO_TARGET, false) && hasShiftDown) {
                boolean autoTargetDisabled = stack.getOrDefault(DataComponents.DISABLE_AUTO_TARGET, false);
                stack.set(DataComponents.DISABLE_AUTO_TARGET, !autoTargetDisabled);
                player.sendOverlayMessage(Component.literal("Auto Target " + (autoTargetDisabled ? "Enabled" : "Disabled")));

                return true;
            }

            return false;
        }, ModeSwitchHandlerRegistry.NORMAL);
    }

    private static boolean disableNightVision(Player player, ItemStack stack) {
        if (stack.is(ModRegistration.CRYSTAL_HELMET.get()) && stack.getOrDefault(DataComponents.NIGHT_VISION, false)) {
            // TODO: Can probably extract this for all of the toggles
            boolean disableNightVision = stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false);
            stack.set(DataComponents.DISABLE_NIGHT_VISION, !disableNightVision);
            player.sendOverlayMessage(Component.literal("Night Vision " + (disableNightVision ? "Enabled" : "Disabled")));
            return true;
        }

        return false;
    }

    private static boolean disableCreativeFlight(Player player, ItemStack stack, SkillData skillData) {
        if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.CREATIVE_FLIGHT, false) && skillData != null) {
            boolean added = AttributeUtils.toggleAttribute(stack, NeoForgeMod.CREATIVE_FLIGHT, skillData);
            player.sendOverlayMessage(Component.literal("Creative Flight " + (added ? "Enabled" : "Disabled")));
            return true;
        }

        return false;
    }

    private static boolean disableFrostWalker(Player player, ItemStack stack) {
        if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.FROST_WALKER, false) || EnchantmentUtils.hasEnchantment(stack, Enchantments.FROST_WALKER)) {
            if (EnchantmentUtils.hasEnchantment(stack, Enchantments.FROST_WALKER)) {
                EnchantmentUtils.removeEnchantment(stack, Enchantments.FROST_WALKER);
                stack.set(dev.willyelton.crystal.core.common.datacomponent.DataComponents.FROST_WALKER, true);

                player.sendOverlayMessage(Component.literal("Frost Walker Disabled"));

                return true;
            } else if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.FROST_WALKER, false)) {
                EnchantmentUtils.addEnchantment(stack, Enchantments.FROST_WALKER, 2, player);

                player.sendOverlayMessage(Component.literal("Frost Walker Enabled"));

                return true;
            }
        }

        return false;
    }
}
