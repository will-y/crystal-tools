package dev.willyelton.crystal.tools.client.gui.component.backpack;

import dev.willyelton.crystal.core.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal.core.common.network.model.BackpackAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MatchContentsButton extends BackpackActionButton {
    public MatchContentsButton(int x, int y, Screen screen, SubScreenContainerMenu container) {
        super(x, y, Component.literal("Match Backpack Contents"), BackpackAction.MATCH_CONTENTS, 60, screen, container);
    }
}
