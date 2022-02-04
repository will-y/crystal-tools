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
                } else {
                    // silk touch or fortune
                    if (EnchantmentUtils.hasEnchantment(tool, Enchantments.SILK_TOUCH)) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.SILK_TOUCH);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.BLOCK_FORTUNE, 3);
                        playerEntity.displayClientMessage(new TextComponent("Mode: Fortune"), true);
                    } else if (EnchantmentUtils.hasEnchantment(tool, Enchantments.BLOCK_FORTUNE)) {
                        EnchantmentUtils.removeEnchantment(tool, Enchantments.BLOCK_FORTUNE);
                        EnchantmentUtils.addEnchantment(tool, Enchantments.SILK_TOUCH, 1);
                        playerEntity.displayClientMessage(new TextComponent("Mode: Silk Touch"), true);
                    }
                }
            }
        }
    }
}
