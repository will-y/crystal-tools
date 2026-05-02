package dev.willyelton.crystal.tools.client.gui;

import dev.willyelton.crystal.core.client.gui.ContainerSubScreen;
import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import dev.willyelton.crystal.tools.common.inventory.container.CrystalQuarryContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuarrySettingsScreen extends ContainerSubScreen<CrystalQuarryContainerMenu, CrystalQuarryScreen> {
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
                .onValueChange((checkbox, checkBoxValue) -> {
                    menu.setSetting(buttonId, checkBoxValue);
                    if (buttonId == 1 && checkBoxValue) {
                        menu.setSetting(11, false);
                        if (fortuneCheckbox != null) {
                            fortuneCheckbox.selected = false;
                        }
                    } else if (buttonId == 2 && checkBoxValue) {
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
        checkbox.textWidget = new MultiLineTextWidget(checkbox.getMessage().copy().withColor(4210752 + 0xFF000000).withoutShadow(), font).setMaxWidth(checkbox.getWidth());

        return checkbox;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawTopBar(guiGraphics);

        for (int i = 0; i < 6; i++) {
            drawEmptyRow(guiGraphics, i);
        }

        drawBottomBar(guiGraphics, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * 6);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752 + 0xFF000000, false);
    }

    @Override
    public Component getButtonName() {
        return Component.translatable("button.crystal_tools.quarry_settings");
    }

    @Override
    public SubScreenType getType() {
        return SubScreenType.QUARRY_SETTINGS;
    }

    @Override
    public int getButtonTextureXOffset() {
        return 60;
    }
}
