package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceInputSlot;
import dev.willyelton.crystal_tools.common.inventory.container.slot.furnace.CrystalFurnaceOutputSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CrystalFurnaceScreen extends BaseMenuUpgradeScreen<CrystalFurnaceContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_furnace.png");
    private static final int SLOT_TEXTURE_X = 176;
    private static final int SLOT_TEXTURE_Y = 27;
    private static final int SLOT_TEXTURE_SIZE = 18;

    private static final int ARROW_TEXTURE_X = 188;
    private static final int ARROW_TEXTURE_ON_X = 176;
    private static final int ARROW_TEXTURE_Y = 13;
    private static final int ARROW_WIDTH = 12;
    private static final int ARROW_HEIGHT = 14;

    private static final int FIRE_TEXTURE_X = 189;
    private static final int FIRE_TEXTURE_ON_X = 176;
    private static final int FIRE_TEXTURE_Y = 0;
    private static final int FIRE_TEXTURE_WIDTH = 14;
    private static final int FIRE_TEXTURE_HEIGHT = 13;

    private static final int FUEL_ARROW_TEXTURE_X = 176;
    private static final int FUEL_ARROW_TEXTURE_Y = 45;
    private static final int FUEL_ARROW_TEXTURE_WIDTH = 8;
    private static final int FUEL_ARROW_TEXTURE_HEIGHT = 4;

    private static final int FUEL_BAR_X = 28;
    private static final int FUEL_BAR_Y = 91;
    private static final int FUEL_BAR_TEXTURE_X = 0;
    private static final int FUEL_BAR_TEXTURE_Y = 191;
    private static final int FUEL_BAR_INITIAL_WIDTH = 44;
    private static final int FUEL_BAR_HEIGHT = 2;
    private static final int FUEL_BAR_WIDTH_INCREASE = 20;

    private static final int FUEL_INSERT_X = 26;
    private static final int FUEL_INSERT_Y = 86;
    private static final int FUEL_INSERT_TEXTURE_X = 0;
    private static final int FUEL_INSERT_TEXTURE_Y = 201;
    private static final int FUEL_INSERT_WIDTH = 6;
    private static final int FUEL_INSERT_HEIGHT = 5;

    private static final int MAX_COUNTER = 15;
    private static final int MAX_ANIM_FRAME = 3;

    private final NonNullList<Slot> slots;

    private int counter = 0;
    private int animFrame = 0;

    public CrystalFurnaceScreen(CrystalFurnaceContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_furnace"));
        this.slots = container.slots;

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.renderSlotsTexture(guiGraphics);
        this.renderFuelBar(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderSlotsTexture(GuiGraphics guiGraphics) {
        for (Slot slot: this.slots) {
            if (slot.isActive()) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x - 1 + this.leftPos, slot.y -1 + this.topPos, SLOT_TEXTURE_X, SLOT_TEXTURE_Y, SLOT_TEXTURE_SIZE, SLOT_TEXTURE_SIZE, 256, 256);
                if (slot instanceof CrystalFurnaceOutputSlot) {
                    // Draw arrow
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + 3 + this.leftPos, slot.y + 18 + this.topPos, ARROW_TEXTURE_X, ARROW_TEXTURE_Y, ARROW_WIDTH, ARROW_HEIGHT, 256, 256);
                    // Draw arrow progress
                    float progress = this.menu.getBurnProgress(slot.index - 5);
                    int height = (int) (progress * ARROW_HEIGHT);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + 3 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + ARROW_HEIGHT - height, ARROW_TEXTURE_ON_X, ARROW_TEXTURE_Y + ARROW_HEIGHT - height, ARROW_WIDTH, height, 256, 256);
                } else if (slot instanceof CrystalFurnaceInputSlot) {
                    // Draw fire below
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + 1 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + 2, FIRE_TEXTURE_X, FIRE_TEXTURE_Y, FIRE_TEXTURE_WIDTH, FIRE_TEXTURE_HEIGHT, 256, 256);
                    // Draw lit progress
                    if (this.menu.isLit()) {
                        float litProgress = this.menu.getLitProgress();
                        int height = (int) (litProgress * FIRE_TEXTURE_HEIGHT);
                        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + 2 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + FIRE_TEXTURE_HEIGHT - height + 2, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_TEXTURE_HEIGHT - height, FIRE_TEXTURE_WIDTH, height, 256, 256);
                    }
                } else if (slot instanceof CrystalFurnaceFuelSlot && slot.index != 10) {
                    // Draw fuel arrow thing
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, slot.x + 4 + this.leftPos, slot.y + 19 + this.topPos, FUEL_ARROW_TEXTURE_X, FUEL_ARROW_TEXTURE_Y, FUEL_ARROW_TEXTURE_WIDTH, FUEL_ARROW_TEXTURE_HEIGHT, 256, 256);
                }
            }
        }
    }

    private void renderFuelBar(GuiGraphics guiGraphics) {
        if (counter > MAX_COUNTER) {
            counter = 0;
            this.animFrame += 1;
            if (this.animFrame > this.MAX_ANIM_FRAME) this.animFrame = 0;
        }
        counter++;

        int widthIncrease = (this.menu.getNumActiveSlots() - 1) * FUEL_BAR_WIDTH_INCREASE;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + FUEL_BAR_X, this.topPos + FUEL_BAR_Y, FUEL_BAR_TEXTURE_X, FUEL_BAR_TEXTURE_Y, FUEL_BAR_INITIAL_WIDTH + widthIncrease, FUEL_BAR_HEIGHT, 256, 256);

        if (this.menu.isLit()) {
            // draw animated fuel bar
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + FUEL_BAR_X, this.topPos + FUEL_BAR_Y, FUEL_BAR_TEXTURE_X, FUEL_BAR_TEXTURE_Y + (this.animFrame + 1) * 2, FUEL_BAR_INITIAL_WIDTH + widthIncrease, FUEL_BAR_HEIGHT, 256, 256);

            // also animate the inserter things
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + FUEL_INSERT_X, this.topPos + FUEL_INSERT_Y, FUEL_INSERT_TEXTURE_X + this.animFrame * FUEL_INSERT_WIDTH, FUEL_INSERT_TEXTURE_Y, FUEL_INSERT_WIDTH, FUEL_INSERT_HEIGHT, 256, 256);
        }
    }
}
