package dev.willyelton.crystal_tools.common.inventory.container.subscreen;

import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Interface for container menus that can have subscreens
 */
public interface SubScreenContainerMenu {
    /**
     * Performs any logic needed to reset the container menu to the state before a subscreen was opened
     */
    void closeSubScreen();

    void openSubScreen(SubScreenType subScreenType);

    default void sendUpdatePacket(BackpackScreenPayload.BackpackAction type) {
        this.sendUpdatePacket(type, false);
    }

    default void sendUpdatePacket(BackpackScreenPayload.BackpackAction type, boolean hasShiftDown) {
        ClientPacketDistributor.sendToServer(new BackpackScreenPayload(type, hasShiftDown));
    }
}
