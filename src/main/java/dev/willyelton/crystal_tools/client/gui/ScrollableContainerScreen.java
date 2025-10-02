package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.inventory.container.ScrollableMenu;
import dev.willyelton.crystal_tools.common.network.data.ContainerRowsPayload;
import dev.willyelton.crystal_tools.common.network.data.ScrollPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public abstract class ScrollableContainerScreen<T extends AbstractContainerMenu & ScrollableMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/gui/scroll_bar.png");
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
        ClientPacketDistributor.sendToServer(new ContainerRowsPayload(getMaxDisplayRows()));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        int i = this.menu.getRowIndexForScroll(this.scrollOffset);
        this.scrollOffset = this.menu.getScrollForRowIndex(i);
        if (i != currentRow) {
            currentRow = i;
            ClientPacketDistributor.sendToServer(new ScrollPayload(i));
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && this.insideScrollbar(event.x(), event.y())) {
            this.scrolling = this.canScroll();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            scrolling = false;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Positive delta is up, negative is down
        if (!this.canScroll()) {
            return false;
        } else {
            currentRow = Mth.clamp(currentRow - (int) scrollY, 0, this.menu.getRows() - this.getMaxDisplayRows());
            this.scrollOffset = this.menu.getScrollForRowIndex(currentRow);
            ClientPacketDistributor.sendToServer(new ScrollPayload(currentRow));
            return true;
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.scrolling) {
            int scrollTop = topPos + scrollY;
            int scrollBottom = scrollTop + scrollHeight;
            this.scrollOffset = ((float) event.y() - (float) scrollTop - HANDLE_HEIGHT / 2.0F) / ((float) (scrollBottom - scrollTop - HANDLE_HEIGHT));
            this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0F, 1.0F);
            int i = this.menu.getRowIndexForScroll(this.scrollOffset);
            if (i != currentRow) {
                currentRow = i;
                ClientPacketDistributor.sendToServer(new ScrollPayload(i));
            }
            return true;
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (this.canScroll()) {
            int scrollXActual = leftPos + scrollX;
            int scrollYActual = topPos + scrollY;
            int scrollYEnd = scrollYActual + scrollHeight;

            // Draw top
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual, topPos, 0, 0, SCROLL_WIDTH + 7, scrollY, 256, 256);

            int heightDrawn = 1;

            // Top Pixel
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual, scrollYActual, SCROLL_WIDTH + 7, 0, SCROLL_WIDTH + 7, 1, 256, 256);

            while (heightDrawn < scrollHeight - 254) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual, scrollYActual + heightDrawn, SCROLL_WIDTH + 7, 1, SCROLL_WIDTH + 7, 254, 256, 256);
                heightDrawn += 254;
            }

            int remainingHeight = scrollHeight - heightDrawn;

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual, scrollYActual + heightDrawn, SCROLL_WIDTH + 7, 256 - remainingHeight, SCROLL_WIDTH + 7, remainingHeight, 256, 256);

            // Draw bottom
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual, scrollYActual + scrollHeight, 0, 251, SCROLL_WIDTH + 7, 5, 256, 256);

            // Draw handle
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, scrollXActual + 1, scrollYActual + 1 + (int) ((float) (scrollYEnd - scrollYActual - 17) * scrollOffset), (SCROLL_WIDTH + 7) * 2, 0, HANDLE_WIDTH, HANDLE_HEIGHT, 256, 256);
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
