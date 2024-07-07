package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.client.gui.component.backpack.ClearFilterButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.MatchContentsButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.WhitelistToggleButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.INVENTORY_WIDTH;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.ROW_HEIGHT;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE_SIZE;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TOP_BAR_HEIGHT;
import static dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload.BackpackAction.PICKUP_BLACKLIST;
import static dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload.BackpackAction.PICKUP_WHITELIST;

public class FilterConfigScreen extends BackpackSubScreen<CrystalBackpackContainerMenu> {
    private boolean whitelist;

    public FilterConfigScreen(CrystalBackpackContainerMenu menu, Inventory playerInventory, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, Component.literal("Filter"), returnScreen);
        this.whitelist = menu.getWhitelist();
    }

    @Override
    protected void init() {
        super.init();

        if (this.menu.getFilterRows() > 0) {
            this.addRenderableWidget(new WhitelistToggleButton(this.leftPos + 157, this.topPos + 4,
                    button -> {
                        whitelist = !whitelist;
                        if (button instanceof WhitelistToggleButton toggleButton) {
                            toggleButton.setWhitelist(whitelist);
                            menu.setWhitelist(whitelist);
                            BackpackScreenPayload.BackpackAction type = whitelist ? PICKUP_WHITELIST : PICKUP_BLACKLIST;
                            menu.sendUpdatePacket(type);
                        }
                    },
                    whitelist, this));

            this.addRenderableWidget(new ClearFilterButton(this.leftPos + 143, this.topPos + 4, this, menu));

            this.addRenderableWidget(new MatchContentsButton(this.leftPos + 129, this.topPos + 4, this, menu));
        }
    }

    @Override
    protected int getRowsToDraw() {
        return Math.min(menu.getFilterRows(), getContainerRows());
    }

    @Override
    protected void drawContentRow(GuiGraphics guiGraphics, int row) {
        guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

    }
}
