package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.levelable.tool.UseMode;
import dev.willyelton.crystal_tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModeSwitchHandler {
    public static final ModeSwitchHandler INSTANCE = new ModeSwitchHandler();

    public void handle(final ModeSwitchPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack tool = ItemStackUtils.getHeldLevelableTool(player);
            if (tool.isEmpty()) {
                // Disable night vision
                player.getArmorSlots().forEach(stack -> {
                    disableNightVision(player, stack);
                });
            }

            // check for upgrade
            if (tool.getOrDefault(DataComponents.MINE_MODE, false)) {
                if (payload.hasShiftDown() && !payload.hasCtrlDown() && !payload.hasAltDown()) {
                    // 3x3 or 1x1 mode
                    if (tool.getOrDefault(DataComponents.HAS_3x3, false)) {
                        boolean disable3x3 = tool.getOrDefault(DataComponents.DISABLE_3x3, false);
                        tool.set(DataComponents.DISABLE_3x3, !disable3x3);
                        player.displayClientMessage(Component.literal("Break Mode: " + (!disable3x3 ? "1x1" : "3x3")), true);
                    }
                } else if (payload.hasCtrlDown() && !payload.hasShiftDown() && !payload.hasAltDown()){
                    // Auto smelt on/off
                    if (tool.getOrDefault(DataComponents.AUTO_SMELT, false)) {
                        boolean disableAutoSmelt = tool.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false);
                        tool.set(DataComponents.DISABLE_AUTO_SMELT, !disableAutoSmelt);
                        player.displayClientMessage(Component.literal("Auto Smelt " + (!disableAutoSmelt ? "Disabled" : "Enabled")), true);
                    }
                } else {
                    // silk touch or fortune
                    if (EnchantmentUtils.hasEnchantment(tool, Enchantments.SILK_TOUCH) && tool.getOrDefault(DataComponents.FORTUNE_BONUS, 0) > 0) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.SILK_TOUCH);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.FORTUNE, 3, player);
                        player.displayClientMessage(Component.literal("Mine Mode: Fortune"), true);
                    } else if (EnchantmentUtils.hasEnchantment(tool, Enchantments.FORTUNE) && tool.getOrDefault(DataComponents.SILK_TOUCH_BONUS, false)) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.FORTUNE);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.SILK_TOUCH, 1, player);
                        player.displayClientMessage(Component.literal("Mine Mode: Silk Touch"), true);
                    }
                }
            }

            if (payload.hasAltDown() && !payload.hasShiftDown() && !payload.hasCtrlDown()) {
                // Use mode for AIOT
                if (tool.is(Registration.CRYSTAL_AIOT.get())) {
                    UseMode currentMode = UseMode.fromString(tool.getOrDefault(DataComponents.USE_MODE, ""));
                    tool.set(DataComponents.USE_MODE, UseMode.nextMode(tool, currentMode).toString());
                    player.displayClientMessage(Component.literal("Mode: " + UseMode.nextMode(tool, currentMode)), true);
                }
            }

            // Backpack
            // TODO: Does this display messages when you don't have the pickup upgrade yet?
            if (tool.is(Registration.CRYSTAL_BACKPACK.get())) {
                boolean pickupDisabled = tool.getOrDefault(DataComponents.PICKUP_DISABLED, false);
                tool.set(DataComponents.PICKUP_DISABLED, !pickupDisabled);
                player.displayClientMessage(Component.literal("Auto Pickup " + (pickupDisabled ? "Enabled" : "Disabled")), true);
            }

            // Trident
            if (tool.getOrDefault(DataComponents.MINE_MODE, false) && tool.is(Registration.CRYSTAL_TRIDENT.get())) {
                boolean riptideDisabled = tool.getOrDefault(DataComponents.RIPTIDE_DISABLED, false);
                tool.set(DataComponents.RIPTIDE_DISABLED, !riptideDisabled);
                player.displayClientMessage(Component.literal("Riptide " + (riptideDisabled ? "Enabled" : "Disabled")), true);
            }

            // Elytra
            if (tool.getOrDefault(DataComponents.CREATIVE_FLIGHT, 0) >= CrystalToolsServerConfig.CREATIVE_FLIGHT_POINTS.get()) {
                boolean creativeFlightDisabled = tool.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false);
                tool.set(DataComponents.DISABLE_CREATIVE_FLIGHT, !creativeFlightDisabled);
                player.displayClientMessage(Component.literal("Creative Flight " + (creativeFlightDisabled ? "Enabled" : "Disabled")), true);
            }

            // Helmet
            disableNightVision(player, tool);
        });
    }

    private void disableNightVision(Player player, ItemStack stack) {
        if (stack.is(Registration.CRYSTAL_HELMET.get()) && stack.getOrDefault(DataComponents.NIGHT_VISION, false)) {
            // TODO: Can probably extract this for all of the toggles
            boolean disableNightVision = stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false);
            stack.set(DataComponents.DISABLE_NIGHT_VISION, !disableNightVision);
            player.displayClientMessage(Component.literal("Night Vision " + (disableNightVision ? "Enabled" : "Disabled")), true);

            if (!disableNightVision) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }
}
