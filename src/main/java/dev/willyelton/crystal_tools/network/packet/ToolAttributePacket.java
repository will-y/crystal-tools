package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Supplier;

public class ToolAttributePacket {
    private final String key;
    private final float value;
    private final int id;
    private final int slotIndex;
    private final int pointsToSpend;

    public ToolAttributePacket(String key, float value, int id, int slotIndex, int pointsToSpend) {
        this.key = key;
        this.value = value;
        this.id = id;
        this.slotIndex = slotIndex;
        this.pointsToSpend = pointsToSpend;
    }

    public ToolAttributePacket(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        this.key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        this.value = buffer.readFloat();
        this.id = buffer.readInt();
        this.slotIndex = buffer.readInt();
        this.pointsToSpend = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.key.length());
        buffer.writeCharSequence(this.key, Charset.defaultCharset());
        buffer.writeFloat(this.value);
        buffer.writeInt(this.id);
        buffer.writeInt(this.slotIndex);
        buffer.writeInt(this.pointsToSpend);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer playerEntity = ctx.get().getSender();
        ItemStack heldTool = getItemStack(playerEntity, this.slotIndex);

        if (!heldTool.isEmpty()) {
            int skillPoints = (int) NBTUtils.getFloatOrAddKey(heldTool, "skill_points");
            if (skillPoints == 0) return;
            int pointsToAdd = Math.min(skillPoints, this.pointsToSpend);

            Enchantment enchantment = enchantmentFromString(this.key);

            if (enchantment != null) {
                // Special cases for silk touch and fortune because mode switch upgrade
                if (enchantment.equals(Enchantments.SILK_TOUCH)) {
                    if (NBTUtils.getFloatOrAddKey(heldTool, "fortune_bonus") == 0) {
                        EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) this.value);
                    }
                } else if (enchantment.equals(Enchantments.BLOCK_FORTUNE)) {
                    if (NBTUtils.getFloatOrAddKey(heldTool, "silk_touch_bonus") == 0) {
                        EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) this.value);
                    }
                } else {
                    EnchantmentUtils.addEnchantment(heldTool, enchantment, (int) this.value);
                }
                // also add it like normal
                NBTUtils.setValue(heldTool, this.key, this.value);
            } else {
                NBTUtils.addValueToTag(heldTool, this.key, this.value * pointsToAdd);
            }

            // update the skill points array
            if (this.id != -1) {
                NBTUtils.addValueToArray(heldTool, "points", this.id, pointsToAdd);
            }
        }
    }

    private ItemStack getItemStack(Player player, int slotIndex) {
        if (slotIndex == -1) {
            return ItemStackUtils.getHeldLevelableTool(player);
        } else if (slotIndex == -2) {
            // Special case for curios backpacks
            List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);
            if (!curiosStacks.isEmpty()) {
                return curiosStacks.get(0);
            } else {
                return ItemStack.EMPTY.copy();
            }
        }

        return player.getInventory().getItem(slotIndex);
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
            case "depth_strider_bonus" -> Enchantments.DEPTH_STRIDER;
            default -> null;
        };
    }
}
