package dev.willyelton.crystal.core.common.inventory.container.subscreen;

import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import dev.willyelton.crystal.core.common.network.model.BackpackAction;

/**
 * Interface for container menus that can have subscreens
 */
public interface SubScreenContainerMenu {
    /**
     * Performs any logic needed to reset the container menu to the state before a subscreen was opened
     */
    default void closeSubScreen() {}

    default void openSubScreen(SubScreenType subScreenType) {}

    default void sendUpdatePacket(BackpackAction type) {
        this.sendUpdatePacket(type, false);
    }

    default void sendUpdatePacket(BackpackAction type, boolean hasShiftDown) {
        // TODO
//        ClientPacketDistributor.sendToServer(new BackpackScreenPayload(type, hasShiftDown));
    }
}
