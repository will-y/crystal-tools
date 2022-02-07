package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.gui.ModGUIs;
import dev.willyelton.crystal_tools.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.network.ModeSwitchPacket;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.system.CallbackI;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || event.phase == TickEvent.Phase.START)
            return;

        ItemStack levelableTool = ItemStackUtils.getHeldLevelableTool(mc.player);

        if (KeyBindings.upgradeMenu.consumeClick()) {
            handleUpgradeMenu(levelableTool);
        }

        if (KeyBindings.modeSwitch.consumeClick()) {
            handleModeSwitch();
        }
    }

    public static void handleUpgradeMenu(ItemStack levelableTool) {
        if (!levelableTool.isEmpty()) {
            ModGUIs.openScreen(new UpgradeScreen(levelableTool));
        }
    }

    /**
     * Handles changing the mining mode (silk touch or fortune)
     */
    public static void handleModeSwitch() {
        PacketHandler.sendToServer(new ModeSwitchPacket(Screen.hasShiftDown(), Screen.hasControlDown(), Screen.hasAltDown()));
    }
}
