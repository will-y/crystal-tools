package dev.willyelton.crystal_tools.event.client;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.gui.ModGUIs;
import dev.willyelton.crystal_tools.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.ModeSwitchPacket;
import dev.willyelton.crystal_tools.network.packet.OpenBackpackPacket;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || event.phase == TickEvent.Phase.START)
            return;

        ItemStack levelableTool = ItemStackUtils.getHeldLevelableTool(mc.player);

        if (KeyBindings.upgradeMenu.consumeClick()) {
            handleUpgradeMenu(levelableTool, mc.player);
        }

        if (KeyBindings.modeSwitch.consumeClick()) {
            handleModeSwitch();
        }

        if (KeyBindings.openBackpack.consumeClick()) {
            handleOpenBackpack();
        }
    }

    public static void handleUpgradeMenu(ItemStack levelableTool, Player player) {
        if (!levelableTool.isEmpty()) {
            ModGUIs.openScreen(new UpgradeScreen(levelableTool, player));
        }
    }

    /**
     * Handles changing the mining mode (silk touch or fortune)
     */
    public static void handleModeSwitch() {
        PacketHandler.sendToServer(new ModeSwitchPacket(Screen.hasShiftDown(), Screen.hasControlDown(), Screen.hasAltDown()));
    }

    public static void handleOpenBackpack() {
        PacketHandler.sendToServer(new OpenBackpackPacket());
    }
}
