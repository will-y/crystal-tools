package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ModeSwitchPacket {
    private final boolean hasShiftDown;

    public ModeSwitchPacket(boolean hasShiftDown) {
        this.hasShiftDown = hasShiftDown;
    }

    public static void encode(ModeSwitchPacket msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.hasShiftDown);
    }

    public static ModeSwitchPacket decode(FriendlyByteBuf buffer) {
        return new ModeSwitchPacket(buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(final ModeSwitchPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity == null) return;

            ItemStack tool = ItemStackUtils.getHeldLevelableTool(playerEntity);
            if (tool.isEmpty()) return;

            // check for upgrade
            if (NBTUtils.getFloatOrAddKey(tool, "mine_mode") > 0) {
                if (msg.hasShiftDown) {
                    // 3x3 or 1x1 mode
                    boolean disable3x3 = NBTUtils.getBoolean(tool, "disable_3x3");
                    NBTUtils.setValue(tool, "disable_3x3", !disable3x3);
                    playerEntity.displayClientMessage(new TextComponent("Break Mode: " + (!disable3x3 ? "1x1" : "3x3")), true);
                } else {
                    // silk touch or fortune
                    if (EnchantmentUtils.hasEnchantment(tool, Enchantments.SILK_TOUCH) && NBTUtils.getFloatOrAddKey(tool, "fortune_bonus") > 0) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.SILK_TOUCH);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.BLOCK_FORTUNE, 3);
                        playerEntity.displayClientMessage(new TextComponent("Mine Mode: Fortune"), true);
                    } else if (EnchantmentUtils.hasEnchantment(tool, Enchantments.BLOCK_FORTUNE) && NBTUtils.getFloatOrAddKey(tool, "silk_touch_bonus") > 0) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.BLOCK_FORTUNE);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.SILK_TOUCH, 1);
                        playerEntity.displayClientMessage(new TextComponent("Mine Mode: Silk Touch"), true);
                    }
                }
            }
        }
    }
}
