package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MatchContentsButton extends BackpackActionButton {
    public MatchContentsButton(int x, int y, Screen screen, CrystalBackpackContainerMenu container) {
        super(x, y, Component.literal("Match Backpack Contents"), BackpackScreenPayload.BackpackAction.MATCH_CONTENTS, 60, screen, container);
    }
}
