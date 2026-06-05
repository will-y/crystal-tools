package dev.willyelton.crystal.tools.client.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.core.common.network.payload.ModeSwitchPayload;
import dev.willyelton.crystal.tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal.tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal.tools.common.network.data.VeinMiningPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;

        if (mc.player == null || level == null) {
            return;
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
            ClientPacketDistributor.sendToServer(new VeinMiningPayload(RegisterKeyBindingsEvent.VEIN_MINE.isDown()));
        }

    }

    /**
     * Handles changing the mining mode (silk touch or fortune)
     */
    public static void handleModeSwitch(boolean magnet) {
        ClientPacketDistributor.sendToServer(new ModeSwitchPayload(Minecraft.getInstance().hasShiftDown(), Minecraft.getInstance().hasControlDown(), Minecraft.getInstance().hasAltDown(), magnet));
    }

    public static void handleOpenBackpack() {
        ClientPacketDistributor.sendToServer(new OpenBackpackPayload(-1));
    }

    public static void handleTriggerRocket() {
        ClientPacketDistributor.sendToServer(new TriggerRocketPayload());
    }
}
