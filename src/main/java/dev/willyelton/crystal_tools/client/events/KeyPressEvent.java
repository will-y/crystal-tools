package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.ModGUIs;
import dev.willyelton.crystal_tools.client.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal_tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal_tools.common.network.data.VeinMiningPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
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
        ClientLevel level = mc.level;

        if (mc.player == null || level == null)
            return;

        if (RegisterKeyBindingsEvent.UPGRADE_MENU.consumeClick()) {
            ItemStack levelableTool = ItemStackUtils.getHeldLevelableTool(mc.player);
            handleUpgradeMenu(levelableTool, mc.player);
        }

        if (RegisterKeyBindingsEvent.MODE_SWITCH.consumeClick()) {
            handleModeSwitch(false);
        }

        if (RegisterKeyBindingsEvent.TOGGLE_MAGNET.consumeClick()) {
            handleModeSwitch(true);
        }

        if (RegisterKeyBindingsEvent.OPEN_BACKPACK.consumeClick()) {
            handleOpenBackpack();
        }

        if (RegisterKeyBindingsEvent.TRIGGER_ROCKET.consumeClick()) {
            handleTriggerRocket();
        }

        // Send vein mining state every 5 ticks
        if (level.getGameTime() % 5 == 0) {
            PacketDistributor.sendToServer(new VeinMiningPayload(RegisterKeyBindingsEvent.VEIN_MINE.isDown()));
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
    public static void handleModeSwitch(boolean magnet) {
        PacketDistributor.sendToServer(new ModeSwitchPayload(Screen.hasShiftDown(), Screen.hasControlDown(), Screen.hasAltDown(), magnet));
    }

    public static void handleOpenBackpack() {
        PacketDistributor.sendToServer(new OpenBackpackPayload(-1));
    }

    public static void handleTriggerRocket() {
        PacketDistributor.sendToServer(new TriggerRocketPayload());
    }
}
