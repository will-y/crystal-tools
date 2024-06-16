package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BackpackSubScreen<T extends CrystalBackpackContainerMenu> extends AbstractContainerScreen<T> {
    private final CrystalBackpackScreen returnScreen;

    public BackpackSubScreen(T menu, Inventory playerInventory, Component title, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void onClose() {
        menu.closeSubScreen();
        PacketHandler.sendToServer(new BackpackScreenPacket(BackpackScreenPacket.Type.CLOSE_SUB_SCREEN));
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.returnScreen);
    }

    public CrystalBackpackScreen getReturnScreen() {
        return returnScreen;
    }
}
