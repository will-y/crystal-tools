package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.ContainerRowsPacket;
import dev.willyelton.crystal_tools.network.packet.ScrollPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ScrollableContainerScreen<T extends AbstractContainerMenu & ScrollableMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/scroll_bar.png");
    protected static final int SCROLL_WIDTH = 14;
    protected static final int HANDLE_WIDTH = 12;
    protected static final int HANDLE_HEIGHT = 15;

    private final int scrollX;
    private final int scrollY;
    // Setter because won't be known at constructor time
    private int scrollHeight;

    private float scrollOffset;
    private boolean scrolling;
    private int currentRow;

    public ScrollableContainerScreen(T menu, Inventory playerInventory, Component title, int scrollX, int scrollY, int scrollHeight) {
        super(menu, playerInventory, title);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.scrollHeight = scrollHeight;
        this.currentRow = 0;
    }

    @Override
    protected void init() {
        super.init();
        // Might need to go in resize
        this.menu.setMaxRows(getMaxDisplayRows());
        menu.setUpSlots();
        PacketHandler.sendToServer(new ContainerRowsPacket(getMaxDisplayRows()));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        int i = this.menu.getRowIndexForScroll(this.scrollOffset);
        this.scrollOffset = this.menu.getScrollForRowIndex(i);
        if (i != currentRow) {
            currentRow = i;
            PacketHandler.sendToServer(new ScrollPacket(i));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.insideScrollbar(mouseX, mouseY)) {
            this.scrolling = this.canScroll();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scrolling = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Positive delta is up, negative is down
        if (!this.canScroll()) {
            return false;
        } else {
            currentRow = Mth.clamp(currentRow - (int) delta, 0, this.menu.getRows() - this.getMaxDisplayRows());
            this.scrollOffset = this.menu.getScrollForRowIndex(currentRow);
            PacketHandler.sendToServer(new ScrollPacket(currentRow));
            return true;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            int scrollTop = topPos + scrollY;
            int scrollBottom = scrollTop + scrollHeight;
            this.scrollOffset = ((float) mouseY - (float) scrollTop - HANDLE_HEIGHT / 2.0F) / ((float) (scrollBottom - scrollTop - HANDLE_HEIGHT));
            this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0F, 1.0F);
            int i = this.menu.getRowIndexForScroll(this.scrollOffset);
            if (i != currentRow) {
                currentRow = i;
                PacketHandler.sendToServer(new ScrollPacket(i));
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (this.canScroll()) {
            int scrollXActual = leftPos + scrollX;
            int scrollYActual = topPos + scrollY;
            int scrollYEnd = scrollYActual + scrollHeight;

            // Draw top
            guiGraphics.blit(TEXTURE, scrollXActual, topPos, 0, 0, SCROLL_WIDTH + 7, scrollY);

            int heightDrawn = 1;

            // Top Pixel
            guiGraphics.blit(TEXTURE, scrollXActual, scrollYActual, SCROLL_WIDTH + 7, 0, SCROLL_WIDTH + 7, 1);

            while (heightDrawn < scrollHeight - 254) {
                guiGraphics.blit(TEXTURE, scrollXActual, scrollYActual + heightDrawn, SCROLL_WIDTH + 7, 1, SCROLL_WIDTH + 7, 254);
                heightDrawn += 254;
            }

            int remainingHeight = scrollHeight - heightDrawn;

            guiGraphics.blit(TEXTURE, scrollXActual, scrollYActual + heightDrawn, SCROLL_WIDTH + 7, 256 - remainingHeight, SCROLL_WIDTH + 7, remainingHeight);

            // Draw bottom
            guiGraphics.blit(TEXTURE, scrollXActual, scrollYActual + scrollHeight, 0, 251, SCROLL_WIDTH + 7, 5);

            // Draw handle
            guiGraphics.blit(TEXTURE, scrollXActual + 1, scrollYActual + 1 + (int) ((float) (scrollYEnd - scrollYActual - 17) * scrollOffset), (SCROLL_WIDTH + 7) * 2, 0, HANDLE_WIDTH, HANDLE_HEIGHT);
        }
    }

    public void setScrollHeight(int scrollHeight) {
        this.scrollHeight = scrollHeight;
    }

    public abstract int getMaxDisplayRows();

    protected boolean canScroll() {
        return menu.canScroll();
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        return mouseX >= leftPos + scrollX && mouseX <= leftPos + scrollX + SCROLL_WIDTH
                && mouseY >= topPos + scrollY && mouseY <= topPos + scrollY + scrollHeight;
    }
}
