package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.network.data.ToolAttributePayload;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToolAttributeHandler {
    public static ToolAttributeHandler INSTANCE = new ToolAttributeHandler();

    public void handle(final ToolAttributePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

            if (!heldTool.isEmpty()) {
                ResourceKey<Enchantment> enchantment = enchantmentFromString(payload.key());

                if (enchantment != null) {
                    // Special cases for silk touch and fortune because mode switch upgrade
                    if (enchantment.equals(Enchantments.SILK_TOUCH)) {
                        if (heldTool.getOrDefault(DataComponents.FORTUNE_BONUS, 0) == 0) {
                            EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) payload.value(), player);
                        }
                        // also add it like normal, only for silk touch and fortune?
                        DataComponents.setValue(heldTool, payload.key(), payload.value());
                    } else if (enchantment.equals(Enchantments.FORTUNE)) {
                        if (!heldTool.getOrDefault(DataComponents.SILK_TOUCH_BONUS, false)) {
                            EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) payload.value(), player);
                        }
                        // also add it like normal, only for silk touch and fortune?
                        DataComponents.setValue(heldTool, payload.key(), payload.value());
                    } else {
                        EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) payload.value(), player);
                    }
                } else {
                    DataComponents.addToComponent(heldTool, payload.key(), payload.value());
                }

                // update the skill points array
                if (payload.id() != -1) {
                    DataComponents.addValueToArray(heldTool, DataComponents.POINTS_ARRAY, payload.id(), 1);
                }
            }
        });
    }

    private static ResourceKey<Enchantment> enchantmentFromString(String string) {
        return switch (string) {
            case "silk_touch_bonus" -> Enchantments.SILK_TOUCH;
            case "fortune_bonus" -> Enchantments.FORTUNE;
            case "looting_bonus" -> Enchantments.LOOTING;
            case "protection_bonus" -> Enchantments.PROTECTION;
            case "fire_protection_bonus" -> Enchantments.FIRE_PROTECTION;
            case "blast_protection_bonus" -> Enchantments.BLAST_PROTECTION;
            case "projectile_protection_bonus" -> Enchantments.PROJECTILE_PROTECTION;
            case "feather_falling_bonus" -> Enchantments.FEATHER_FALLING;
            case "soul_speed_bonus" -> Enchantments.SOUL_SPEED;
            case "frost_walker_bonus" -> Enchantments.FROST_WALKER;
            case "aqua_affinity_bonus" -> Enchantments.AQUA_AFFINITY;
            case "respiration_bonus" -> Enchantments.RESPIRATION;
            case "thorns_bonus" -> Enchantments.THORNS;
            case "depth_strider_bonus" -> Enchantments.DEPTH_STRIDER;
            default -> null;
        };
    }
}
