package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.ROW_HEIGHT;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TOP_BAR_HEIGHT;

public class QuarrySettingsScreen extends BackpackSubScreen<CrystalQuarryContainerMenu, CrystalQuarryScreen> {
    private Checkbox silkTouchCheckbox;
    private Checkbox fortuneCheckbox;

    public QuarrySettingsScreen(CrystalQuarryContainerMenu menu, Inventory playerInventory, CrystalQuarryScreen returnScreen) {
        super(menu, playerInventory, Component.literal("Quarry Settings"), returnScreen);
    }

    @Override
    protected void init() {
        super.init();

        int index = 0;
        addCheckbox("Fill With Dirt", "Causes the Quarry to fill in mined blocks with dirt",
                0, index++);
        if (menu.shouldSettingBeActive(1)) {
            silkTouchCheckbox = addCheckbox("Enable Silk Touch", "Enables Silk Touch for the quarry",
                    1, index++);
        }
        if (menu.shouldSettingBeActive(2)) {
            fortuneCheckbox = addCheckbox("Enable Fortune", "Enables Fortune for the quarry",
                    2, index++);
        }
        if (menu.shouldSettingBeActive(3)) {
            addCheckbox("Enable Auto Output", "Enables Auto Output for the quarry",
                    3, index++);
        }

    }

    private Checkbox addCheckbox(String title, String tooltip, int buttonId, int index) {
       return this.addRenderableWidget(fixCheckbox(Checkbox.builder(Component.literal(title), font)
                .pos(leftPos + 4, topPos + 20 + 20 * index)
                .selected(menu.getSetting(buttonId))
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .onValueChange((checkbox, value) -> {
                    menu.setSetting(buttonId, value);
                    if (buttonId == 1 && value) {
                        menu.setSetting(11, false);
                        if (fortuneCheckbox != null) {
                            fortuneCheckbox.selected = false;
                        }
                    } else if (buttonId == 2 && value) {
                        menu.setSetting(10, false);
                        if (silkTouchCheckbox != null) {
                            silkTouchCheckbox.selected = false;
                        }
                    }
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
                })
                .build()
        ));
    }

    // This is all to just change the text color and remove the shadow
    // Might want to make my own component so I don't need to do this
    private Checkbox fixCheckbox(Checkbox checkbox) {
        checkbox.textWidget = new MultiLineTextWidget(checkbox.getMessage(), font) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                MultiLineLabel multilinelabel = this.cache.getValue(this.getFreshCacheKey());
                int i = this.getX();
                int j = this.getY();
                int k = 9;
                int l = this.getColor();
                if (this.centered) {
                    multilinelabel.renderCentered(guiGraphics, i + this.getWidth() / 2, j, k, l);
                } else {
                    // Just want to change this
                    multilinelabel.renderLeftAlignedNoShadow(guiGraphics, i, j, k, l);
                }
            }
        }.setMaxWidth(checkbox.getWidth()).setColor(4210752);

        return checkbox;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        drawTopBar(guiGraphics);

        for (int i = 0; i < 6; i++) {
            drawEmptyRow(guiGraphics, i);
        }

        drawBottomBar(guiGraphics, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * 6);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected int getRowsToDraw() {
        // Unused here
        return 0;
    }

    @Override
    protected void drawContentRow(GuiGraphics guiGraphics, int row) {

    }

    @Override
    public Component getButtonName() {
        return Component.literal("Quarry Settings");
    }

    @Override
    public SubScreenType getType() {
        return SubScreenType.SETTINGS;
    }

    @Override
    public int getButtonTextureXOffset() {
        return 60;
    }
}
