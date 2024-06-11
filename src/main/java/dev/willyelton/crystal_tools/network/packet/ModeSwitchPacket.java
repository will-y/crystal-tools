package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.levelable.tool.UseMode;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ModeSwitchPacket {
    private final boolean hasShiftDown;
    private final boolean hasCtrlDown;
    private final boolean hasAltDown;

    public ModeSwitchPacket(boolean hasShiftDown, boolean hasCtrlDown, boolean hasAltDown) {
        this.hasShiftDown = hasShiftDown;
        this.hasCtrlDown = hasCtrlDown;
        this.hasAltDown = hasAltDown;
    }

    public ModeSwitchPacket(FriendlyByteBuf buffer) {
        this.hasShiftDown = buffer.readBoolean();
        this.hasCtrlDown = buffer.readBoolean();
        this.hasAltDown = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.hasShiftDown);
        buffer.writeBoolean(this.hasCtrlDown);
        buffer.writeBoolean(this.hasAltDown);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer playerEntity = ctx.get().getSender();
        if (playerEntity == null) return;

        ItemStack tool = ItemStackUtils.getHeldLevelableTool(playerEntity);
        if (tool.isEmpty()) return;

        // check for upgrade
        if (NBTUtils.getFloatOrAddKey(tool, "mine_mode") > 0) {
            if (this.hasShiftDown && !this.hasCtrlDown && !this.hasAltDown) {
                // 3x3 or 1x1 mode
                if (NBTUtils.getFloatOrAddKey(tool, "3x3") > 0) {
                    boolean disable3x3 = NBTUtils.getBoolean(tool, "disable_3x3");
                    NBTUtils.setValue(tool, "disable_3x3", !disable3x3);
                    playerEntity.displayClientMessage(Component.literal("Break Mode: " + (!disable3x3 ? "1x1" : "3x3")), true);
                }
            } else if (this.hasCtrlDown && !this.hasShiftDown && !this.hasAltDown){
                // Auto smelt on/off
                if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0) {
                    boolean disableAutoSmelt = NBTUtils.getBoolean(tool, "disable_auto_smelt");
                    NBTUtils.setValue(tool, "disable_auto_smelt", !disableAutoSmelt);
                    playerEntity.displayClientMessage(Component.literal("Auto Smelt " + (!disableAutoSmelt ? "Disabled" : "Enabled")), true);
                }
            } else {
                // silk touch or fortune
                if (EnchantmentUtils.hasEnchantment(tool, Enchantments.SILK_TOUCH) && NBTUtils.getFloatOrAddKey(tool, "fortune_bonus") > 0) {
                    EnchantmentUtils.removeEnchantment(tool, Enchantments.SILK_TOUCH);
                    EnchantmentUtils.addEnchantment(tool, Enchantments.BLOCK_FORTUNE, 3);
                    playerEntity.displayClientMessage(Component.literal("Mine Mode: Fortune"), true);
                } else if (EnchantmentUtils.hasEnchantment(tool, Enchantments.BLOCK_FORTUNE) && NBTUtils.getFloatOrAddKey(tool, "silk_touch_bonus") > 0) {
                    EnchantmentUtils.removeEnchantment(tool, Enchantments.BLOCK_FORTUNE);
                    EnchantmentUtils.addEnchantment(tool, Enchantments.SILK_TOUCH, 1);
                    playerEntity.displayClientMessage(Component.literal("Mine Mode: Silk Touch"), true);
                }
            }
        }

        if (this.hasAltDown && !this.hasShiftDown && !this.hasCtrlDown) {
            // Use mode for AIOT
            if (tool.is(Registration.CRYSTAL_AIOT.get())) {
                UseMode currentMode = UseMode.fromString(NBTUtils.getString(tool, "use_mode"));
                NBTUtils.setValue(tool, "use_mode", UseMode.nextMode(tool, currentMode).toString());
                playerEntity.displayClientMessage(Component.literal("Mode: " + UseMode.nextMode(tool, currentMode)), true);
            }
        }

        // Backpack
        if (tool.is(Registration.CRYSTAL_BACKPACK.get())) {
            boolean pickupDisabled = NBTUtils.getBoolean(tool, "pickup_disabled", false);
            NBTUtils.setValue(tool, "pickup_disabled", !pickupDisabled);
            playerEntity.displayClientMessage(Component.literal("Auto Pickup " + (pickupDisabled ? "Enabled" : "Disabled")), true);
        }

        // Trident
        if (NBTUtils.getBoolean(tool, "mode_switch") && tool.is(Registration.CRYSTAL_TRIDENT.get())) {
            boolean riptideDisabled = NBTUtils.getBoolean(tool, "riptide_disabled");
            NBTUtils.setValue(tool, "riptide_disabled", !riptideDisabled);
            playerEntity.displayClientMessage(Component.literal("Riptide " + (riptideDisabled ? "Enabled" : "Disabled")), true);
        }
    }
}
