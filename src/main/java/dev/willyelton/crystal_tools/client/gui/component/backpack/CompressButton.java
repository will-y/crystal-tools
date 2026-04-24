package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.api.common.network.model.BackpackAction;
import dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.network.chat.Component;

public class CompressButton extends BackpackActionButton {
    public CompressButton(int x, int y, CrystalBackpackScreen screen, CrystalBackpackContainerMenu container) {
        super(x, y, Component.literal("Compress"), BackpackAction.COMPRESS, 36, screen, container);
    }
}
