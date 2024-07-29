package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.network.chat.Component;

public class SortButton extends BackpackActionButton {
    public SortButton(int x, int y, CrystalBackpackScreen screen, CrystalBackpackContainerMenu container) {
        super(x, y, Component.literal("Sort"), BackpackScreenPayload.BackpackAction.SORT, 24, screen, container);
    }
}
