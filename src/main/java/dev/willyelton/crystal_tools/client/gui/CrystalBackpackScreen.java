package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.backpack.BackpackScreenButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.CompressButton;
import dev.willyelton.crystal_tools.client.gui.component.backpack.SortButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrystalBackpackScreen extends ScrollableContainerScreen<CrystalBackpackContainerMenu> implements SubScreenContainerScreen {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/gui/crystal_backpack.png");

    static final int TEXTURE_SIZE = 512;
    static final int INVENTORY_WIDTH = 176;
    static final int INVENTORY_HEIGHT = 96;
    static final int TOP_BAR_HEIGHT = 17;
    static final int ROW_HEIGHT = 18;

    private final CrystalBackpackContainerMenu container;

    public CrystalBackpackScreen(CrystalBackpackContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name, INVENTORY_WIDTH - 3, TOP_BAR_HEIGHT, 128);
        this.container = container;
        this.inventoryLabelX = 8;
    }

    private void setHeights() {
        int rowsToDraw = getDisplayRows();

        this.inventoryLabelY = rowsToDraw * ROW_HEIGHT + CrystalBackpackContainerMenu.START_Y + 2;
        this.imageHeight = TOP_BAR_HEIGHT + INVENTORY_HEIGHT + rowsToDraw * ROW_HEIGHT;
        this.imageWidth = INVENTORY_WIDTH;
        if (rowsToDraw > menu.getRows()) {
            this.imageWidth += SCROLL_WIDTH + 4;
        }

        this.setScrollHeight(rowsToDraw * ROW_HEIGHT);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // TODO (PORTING): remove a shader set texture call here, should be fine because it is specified below

        int rowsToDraw = getDisplayRows();

        // Backpack top bar
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < rowsToDraw; row++) {
            // Backpack row
            guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Player inventory
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowsToDraw, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
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
        int screenButtonY = this.topPos + 17;

        if (this.menu.canSort()) {
            this.addRenderableWidget(new SortButton(actionButtonX, this.topPos + 4, this, container));
            actionButtonX -= 14;
        }

        if (this.menu.canCompress()) {
            this.addRenderableWidget(new CompressButton(actionButtonX, this.topPos + 4, this, container));
            actionButtonX -=14;
        }

        this.addRenderableWidget(new BackpackScreenButton(this.leftPos - 21, screenButtonY, Component.literal("Open Skill Tree"),
                button -> {
                    this.onClose();
                    // TODO: Get data
                    ModGUIs.openScreen(new UpgradeScreen(menu.getSlotIndex(), menu.getPlayer(), () -> PacketDistributor.sendToServer(new OpenBackpackPayload(menu.getSlotIndex())), null, null));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Open Skill Tree");
                    guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalBackpackScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                }, 40));
        screenButtonY += 21;

        List<BackpackScreenButton> subScreenButtons = getSideButtons(this.leftPos - 21, screenButtonY, this.width, menu);
        subScreenButtons.forEach(this::addRenderableWidget);
    }

    @Override
    public List<BackpackSubScreen<?, ?>> getSubScreens() {
        List<BackpackSubScreen<?, ?>> subScreens = new ArrayList<>();

        if (this.menu.getFilterRows() > 0) {
            subScreens.add(new FilterConfigScreen<>(menu, menu.getPlayerInventory(), this, true));
        }

        if (this.menu.canCompress()) {
            subScreens.add(new CompressConfigScreen(menu, menu.getPlayerInventory(), this));
        }

        return subScreens;
    }
}
