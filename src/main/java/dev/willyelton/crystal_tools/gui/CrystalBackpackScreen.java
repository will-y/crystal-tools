package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.gui.component.CompressButton;
import dev.willyelton.crystal_tools.gui.component.SortButton;
import dev.willyelton.crystal_tools.gui.component.WhitelistToggleButton;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket.Type.*;

public class CrystalBackpackScreen extends ScrollableContainerScreen<CrystalBackpackContainerMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/crystal_backpack.png");

    public static final int TEXTURE_SIZE = 512;
    public static final int INVENTORY_WIDTH = 176;
    public static final int INVENTORY_HEIGHT = 96;
    public static final int TOP_BAR_HEIGHT = 17;
    public static final int ROW_HEIGHT = 18;

    private final CrystalBackpackContainerMenu container;

    public CrystalBackpackScreen(CrystalBackpackContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name, INVENTORY_WIDTH - 3, TOP_BAR_HEIGHT, 128);
        this.container = container;
        this.inventoryLabelX = 8;
    }

    private void setHeights() {
        // Sets up all of the variables, needs to be able to be re called on resize
        int rowsToDraw = getDisplayRows();

        this.inventoryLabelY = rowsToDraw * ROW_HEIGHT + CrystalBackpackContainerMenu.START_Y + 2;
        this.imageHeight = TOP_BAR_HEIGHT + INVENTORY_HEIGHT + rowsToDraw * ROW_HEIGHT;
        this.imageWidth = INVENTORY_WIDTH;
        // TODO: incorporate this into submenus?
        if (rowsToDraw > menu.getRows()) {
            this.imageWidth += SCROLL_WIDTH + 4;
        }
//        if (menu.getFilterRows() > 0) {
//            this.imageWidth += 100;
//        }

        this.setScrollHeight(rowsToDraw * ROW_HEIGHT);
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

        int rowsToDraw = getDisplayRows();

        // Backpack top bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < rowsToDraw; row++) {
            // Backpack row
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Player inventory
        guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowsToDraw, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        // Filter
//        if (container.getFilterRows() > 0) {
//            int leftOffset = canScroll() ? SCROLL_WIDTH + 4 : 0;
//            drawFilter(guiGraphics, leftPos + INVENTORY_WIDTH + leftOffset - 3, topPos, container.getFilterRows());
//        }
    }

    public int getDisplayRows() {
        if (getMaxDisplayRows() <= 0) {
            return menu.getRows();
        } else {
            return Math.min(menu.getRows(), getMaxDisplayRows());
        }
    }

    @Override
    public int getMaxDisplayRows() {
        return (this.height - TOP_BAR_HEIGHT - INVENTORY_HEIGHT) / ROW_HEIGHT;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        // TODO: Should be fixed, don't want to rn
        this.onClose();
        super.resize(minecraft, width, height);
    }

    @Override
    protected void init() {
        setHeights();
        super.init();

        int actionButtonX = this.leftPos + 157;

        if (this.menu.canSort()) {
            this.addRenderableWidget(new SortButton(actionButtonX, this.topPos + 4,
                    button -> container.sendUpdatePacket(SORT),
                    (button, guiGraphics, mouseX, mouseY) -> {
                        Component textComponent = Component.literal("Sort");
                        guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                    }));
            actionButtonX -= 14;
        }

        if (this.menu.canCompress()) {
            this.addRenderableWidget(new CompressButton(actionButtonX, this.topPos + 4,
                    button -> container.sendUpdatePacket(COMPRESS),
                    (button, guiGraphics, mouseX, mouseY) -> {
                        Component textComponent = Component.literal("Compress");
                        guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                    }));
            actionButtonX -= 14;
        }

        // TODO: Put these somewhere
        this.addRenderableWidget(new WhitelistToggleButton(this.leftPos + INVENTORY_WIDTH, this.topPos,
                button -> {
                    menu.openCompressionScreen();
                    PacketHandler.sendToServer(new BackpackScreenPacket(OPEN_COMPRESSION));
                    ModGUIs.openScreen(new CompressConfigScreen(menu, menu.getPlayerInventory(), this));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Compression Screen");
                    guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                },
                true));

        this.addRenderableWidget(new WhitelistToggleButton(this.leftPos + INVENTORY_WIDTH + 14, this.topPos,
                button -> {
                    menu.openFilterScreen();
                    PacketHandler.sendToServer(new BackpackScreenPacket(OPEN_FILTER));
                    ModGUIs.openScreen(new FilterConfigScreen(menu, menu.getPlayerInventory(), this));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Filter Screen");
                    guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                },
                true));
    }

    private void drawFilter(GuiGraphics guiGraphics, int x, int y, int rows) {
        // Draw top
        guiGraphics.blit(TEXTURE, x, y, 0, 240, 103, 17, TEXTURE_SIZE, TEXTURE_SIZE);

        // Draw rows
        for (int i = 0; i < rows; i++) {
            guiGraphics.blit(TEXTURE, x, y + 17 + 18 * i, 0, 257, 103, 18, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Draw bottom
        guiGraphics.blit(TEXTURE, x, y + 17 + 18 * rows, 0, 275, 103, 6, TEXTURE_SIZE, TEXTURE_SIZE);

        // Draw text
        guiGraphics.drawString(this.font, Component.literal("Pickup Filter"), x + 8, topPos + this.titleLabelY, 4210752, false);
    }
}
