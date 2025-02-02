package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.client.gui.component.backpack.BackpackScreenButton;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public interface SubScreenContainerScreen {
    int Y_OFFSET = 21;

    /**
     * Should return a list of {@link BackpackSubScreen} that should currently be enabled.
     * Will get called by {@link SubScreenContainerScreen#getSideButtons(int subScreenButtonX, int subScreenButtonStartingY, int width, SubScreenContainerMenu menu)} to create the actual buttons.
     * @return A list of sub screens that should be enabled
     */
    List<BackpackSubScreen<?, ?>> getSubScreens();

    /**
     * Gets a list of actual buttons. These should be added to the screen with addRenderableWidget.
     * This should not be overwritten.
     * @return A list of buttons to add to the screen
     */
    default List<BackpackScreenButton> getSideButtons(int subScreenButtonX, int subScreenButtonStartingY, int width,
                                                      SubScreenContainerMenu menu) {
        Font font = Minecraft.getInstance().font;
        List<BackpackScreenButton> buttons = new ArrayList<>();

        for (BackpackSubScreen<?, ?> subScreen : getSubScreens()) {
            buttons.add(new BackpackScreenButton(subScreenButtonX, subScreenButtonStartingY, subScreen.getButtonName(),
                    button -> {
                        menu.openSubScreen(subScreen.getType());
                        PacketDistributor.sendToServer(new BackpackScreenPayload(BackpackScreenPayload.BackpackAction.fromSubScreenType(subScreen.getType())));
                        ModGUIs.openScreen(subScreen);
                    },
                    (button, guiGraphics, mouseX, mouseY) -> {
                        guiGraphics.renderTooltip(font, font.split(subScreen.getButtonName(), Math.max(width / 2 - 43, 170)), mouseX, mouseY);
                    }, subScreen.getButtonTextureXOffset()));

            subScreenButtonStartingY += Y_OFFSET;
        }

        return buttons;
    }

    default int getDisplayRows() {
        return 3;
    }
}
