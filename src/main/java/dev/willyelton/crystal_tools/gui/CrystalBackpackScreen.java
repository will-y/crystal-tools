package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.gui.component.WhitelistToggleButton;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket.Type.PICKUP_BLACKLIST;
import static dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket.Type.PICKUP_WHITELIST;

public class CrystalBackpackScreen extends AbstractContainerScreen<CrystalBackpackContainerMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/crystal_backpack.png");

    private static final int TEXTURE_SIZE = 512;

    private final CrystalBackpackContainerMenu container;

    private boolean whitelist;

    public CrystalBackpackScreen(CrystalBackpackContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.container = container;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = container.getRows() * 18 + CrystalBackpackContainerMenu.START_Y + 2;
        whitelist = container.getWhitelist();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        // Backpack top bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, 17, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < container.getRows(); row++) {
            // Backpack row
            guiGraphics.blit(TEXTURE, leftPos, topPos + 17 + 18 * row, 0, 222, 176, 18, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Player inventory
        guiGraphics.blit(TEXTURE, leftPos, topPos + 17 + 18 * container.getRows(), 0, 125, 176, 96, TEXTURE_SIZE, TEXTURE_SIZE);

        // Filter
        if (container.getFilterRows() > 0) {
            drawFilter(guiGraphics, leftPos + 173, topPos, container.getFilterRows());
        }
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new WhitelistToggleButton(this.leftPos, this.topPos - 30,
                button -> {
                    whitelist = !whitelist;
                    if (button instanceof WhitelistToggleButton toggleButton) {
                        toggleButton.setWhitelist(whitelist);
                        // Stays focused after click for some reason
                        toggleButton.setFocused(false);
                        container.setWhitelist(whitelist);
                        BackpackScreenPacket.Type type = whitelist ? PICKUP_WHITELIST : PICKUP_BLACKLIST;
                        container.sendUpdatePacket(type);

                    }
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Whitelist");
                    guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                },
                whitelist, false));
    }

    private void drawFilter(GuiGraphics guiGraphics, int x, int y, int rows) {
        // Draw text
        guiGraphics.drawString(this.font, Component.literal("Pickup Filter"), leftPos + this.titleLabelX + 173, topPos + this.titleLabelY, 4210752, false);

        // Draw top
        guiGraphics.blit(TEXTURE, x, y, 0, 240, 103, 17, TEXTURE_SIZE, TEXTURE_SIZE);

        // Draw rows
        for (int i = 0; i < rows; i++) {
            guiGraphics.blit(TEXTURE, x, y + 17 + 18 * i, 0, 257, 103, 18, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Draw bottom
        guiGraphics.blit(TEXTURE, x, y + 17 + 18 * rows, 0, 275, 103, 6, TEXTURE_SIZE, TEXTURE_SIZE);
    }
}
