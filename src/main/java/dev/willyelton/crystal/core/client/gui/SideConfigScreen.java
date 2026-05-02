package dev.willyelton.crystal.core.client.gui;

import dev.willyelton.crystal.core.client.gui.component.SideConfigButton;
import dev.willyelton.crystal.core.common.block.entity.model.SideConfigOption;
import dev.willyelton.crystal.core.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import dev.willyelton.crystal.core.common.inventory.container.subscreen.SideConfigContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal.core.client.gui.component.SideConfigButton.BUTTON_SIZE;

public class SideConfigScreen<T extends BaseContainerMenu & SideConfigContainerMenu> extends ContainerSubScreen<T, BaseMenuUpgradeScreen<?>> {
    private static final int BUTTON_GAP = 5;

    public SideConfigScreen(T menu, Inventory playerInventory, BaseMenuUpgradeScreen<?> returnScreen) {
        super(menu, playerInventory, Component.literal("Side Config"), returnScreen);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2 - BUTTON_SIZE / 2;
        int topY = height / 2 - (int) (BUTTON_SIZE * 2.5F + BUTTON_GAP);
        addButton(centerX, topY, Direction.UP);
        addButton(centerX, topY + BUTTON_SIZE + BUTTON_GAP, Direction.NORTH);
        addButton(centerX, topY + (BUTTON_SIZE + BUTTON_GAP) * 2, Direction.DOWN);
        addButton(centerX - (BUTTON_SIZE + BUTTON_GAP), topY + (BUTTON_SIZE + BUTTON_GAP), Direction.EAST);
        addButton(centerX + (BUTTON_SIZE + BUTTON_GAP), topY + (BUTTON_SIZE + BUTTON_GAP), Direction.WEST);
        addButton(centerX + (BUTTON_SIZE + BUTTON_GAP), topY + (BUTTON_SIZE + BUTTON_GAP) * 2, Direction.SOUTH);
    }

    private void addButton(int x, int y, Direction direction) {
        addRenderableWidget(new SideConfigButton(x, y, Component.literal(direction.getName()), this::onPress, (button, guiGraphics, mouseX, mouseY) -> {
            if (button instanceof SideConfigButton sideConfigButton) {
                guiGraphics.setTooltipForNextFrame(getMinecraft().font, getMinecraft().font.split(sideConfigButton.tooltip(), Math.max(width / 2 - 43, 170)), mouseX, mouseY);
            }
        }, menu.getSideConfig(direction), direction));
    }

    @Override
    public Component getButtonName() {
        return Component.translatable("button.crystal_tools.auto_output_settings");
    }

    @Override
    public SubScreenType getType() {
        return SubScreenType.SIDE_SETTINGS;
    }

    @Override
    public int getButtonTextureXOffset() {
        return 80;
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

    private SideConfigOption onPress(SideConfigButton button, InputWithModifiers input) {
        SideConfigOption next;
        if (input.hasShiftDown() && menu.supports(SideConfigOption.NONE)) {
            next = SideConfigOption.NONE;
        } else {
            next = menu.next(button.option());
        }

        menu.setSideConfig(button.side(), next);

       return next;
    }
}
