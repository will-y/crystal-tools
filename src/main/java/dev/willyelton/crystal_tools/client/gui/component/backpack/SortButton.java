package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.api.common.network.model.BackpackAction;
import dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.network.chat.Component;

public class SortButton extends BackpackActionButton {
    public SortButton(int x, int y, CrystalBackpackScreen screen, CrystalBackpackContainerMenu container) {
        super(x, y, Component.literal("Sort"), BackpackAction.SORT, 24, screen, container);
    }
}
