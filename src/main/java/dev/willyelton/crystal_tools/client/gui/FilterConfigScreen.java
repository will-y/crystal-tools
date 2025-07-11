package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.client.gui.component.backpack.ClearFilterButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.MatchContentsButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.WhitelistToggleButton;
import dev.willyelton.crystal_tools.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.FilterContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.*;
import static dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload.BackpackAction.PICKUP_BLACKLIST;
import static dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload.BackpackAction.PICKUP_WHITELIST;

public class FilterConfigScreen<T extends BaseContainerMenu & SubScreenContainerMenu & FilterContainerMenu, U extends Screen & SubScreenContainerScreen> extends BackpackSubScreen<T, U> {
    private boolean whitelist;
    private final boolean showMatchBackpackButton;

    public FilterConfigScreen(T menu, Inventory playerInventory, U returnScreen, boolean showMatchBackpackButton) {
        this(menu, playerInventory, returnScreen, Component.literal("Filter"), showMatchBackpackButton);
    }

    public FilterConfigScreen(T menu, Inventory playerInventory, U returnScreen, Component screenTitle) {
        this(menu, playerInventory, returnScreen, screenTitle, false);
    }

    public FilterConfigScreen(T menu, Inventory playerInventory, U returnScreen, Component screenTitle, boolean showMatchBackpackButton) {
        super(menu, playerInventory, screenTitle, returnScreen);
        this.showMatchBackpackButton = showMatchBackpackButton;
    }

    @Override
    protected void init() {
        super.init();
        this.whitelist = this.menu.getWhitelist();

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

            if (showMatchBackpackButton) {
                this.addRenderableWidget(new MatchContentsButton(this.leftPos + 129, this.topPos + 4, this, menu));
            }
        }
    }

    @Override
    protected int getRowsToDraw() {
        return Math.min(menu.getFilterRows(), getContainerRows());
    }

    @Override
    protected void drawContentRow(GuiGraphics guiGraphics, int row) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    public Component getButtonName() {
        return Component.literal("Configure Filters");
    }

    @Override
    public SubScreenType getType() {
        return SubScreenType.FILTER;
    }

    @Override
    public int getButtonTextureXOffset() {
        return 0;
    }
}
