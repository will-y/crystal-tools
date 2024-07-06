package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.client.gui.component.backpack.ClearFilterButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.MatchContentsButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.WhitelistToggleButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.INVENTORY_HEIGHT;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int containerRows = getContainerRows();
        int filterRows = Math.min(menu.getFilterRows(), containerRows);

        // Top Bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < filterRows; row++) {
            // Filter row
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        for (int row = 0; row < containerRows - filterRows; row++) {
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * (row + filterRows), 0, 8, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Inventory
        // TODO: 1.21 this part and top part can be in base class
        guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * containerRows, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
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
}
