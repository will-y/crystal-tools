package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.gui.ModGUIs;
import dev.willyelton.crystal_tools.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
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
        PacketDistributor.sendToServer(new ModeSwitchPayload(Screen.hasShiftDown(), Screen.hasControlDown(), Screen.hasAltDown()));
    }

    public static void handleOpenBackpack() {
        PacketDistributor.sendToServer(new OpenBackpackPayload());
    }
}
