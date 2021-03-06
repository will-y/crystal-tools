package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class ToolAttributePacket {
    private final String key;
    private final float value;
    private final int id;

    public ToolAttributePacket(String key, float value, int id) {
        this.key = key;
        this.value = value;
        this.id = id;
    }

    public static void encode(ToolAttributePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.key.length());
        buffer.writeCharSequence(msg.key, Charset.defaultCharset());
        buffer.writeFloat(msg.value);
        buffer.writeInt(msg.id);
    }

    public static ToolAttributePacket decode(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        String key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        return new ToolAttributePacket(key, buffer.readFloat(), buffer.readInt());
    }

    public static class Handler {
        public static void handle(final ToolAttributePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerEntity = ctx.get().getSender();
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(playerEntity);

            if (!heldTool.isEmpty()) {
                Enchantment enchantment = enchantmentFromString(msg.key);

                if (enchantment != null) {
                    // Special cases for silk touch and fortune because mode switch upgrade
                    if (enchantment.equals(Enchantments.SILK_TOUCH)) {
                        if (NBTUtils.getFloatOrAddKey(heldTool, "fortune_bonus") == 0) {
                            EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) msg.value);
                        }
                    } else if (enchantment.equals(Enchantments.BLOCK_FORTUNE)) {
                        if (NBTUtils.getFloatOrAddKey(heldTool, "silk_touch_bonus") == 0) {
                            EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) msg.value);
                        }
                    } else {
                        EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) msg.value);
                    }
                    // also add it like normal
                    NBTUtils.setValue(heldTool, msg.key, msg.value);
                } else if (msg.key.equals("auto_repair")) {
                    NBTUtils.setValue(heldTool, msg.key, true);
                    NBTUtils.addValueToTag(heldTool, "auto_repair_amount", 1);
                } else {
                    NBTUtils.addValueToTag(heldTool, msg.key, msg.value);
                }

                // update the skill points array
                if (msg.id != -1) {
                    NBTUtils.addValueToArray(heldTool, "points", msg.id, 1);
                }
            }
        }
    }

    private static Enchantment enchantmentFromString(String string) {
        return switch (string) {
            case "silk_touch_bonus" -> Enchantments.SILK_TOUCH;
            case "fortune_bonus" -> Enchantments.BLOCK_FORTUNE;
            case "looting_bonus" -> Enchantments.MOB_LOOTING;
            case "protection_bonus" -> Enchantments.ALL_DAMAGE_PROTECTION;
            case "fire_protection_bonus" -> Enchantments.FIRE_PROTECTION;
            case "blast_protection_bonus" -> Enchantments.BLAST_PROTECTION;
            case "projectile_protection_bonus" -> Enchantments.PROJECTILE_PROTECTION;
            case "feather_falling_bonus" -> Enchantments.FALL_PROTECTION;
            case "soul_speed_bonus" -> Enchantments.SOUL_SPEED;
            case "frost_walker_bonus" -> Enchantments.FROST_WALKER;
            case "aqua_affinity_bonus" -> Enchantments.AQUA_AFFINITY;
            case "respiration_bonus" -> Enchantments.RESPIRATION;
            case "thorns_bonus" -> Enchantments.THORNS;
            default -> null;
        };
    }
}
