package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BackpackSubScreen<T extends CrystalBackpackContainerMenu> extends AbstractContainerScreen<T> {
    private final Screen returnScreen;

    public BackpackSubScreen(T menu, Inventory playerInventory, Component title, Screen returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.returnScreen);
    }
}
