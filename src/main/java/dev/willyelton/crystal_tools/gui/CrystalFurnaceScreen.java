package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceInputSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceOutputSlot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceScreen extends AbstractContainerScreen<CrystalFurnaceContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/crystal_furnace.png");
    private static final int ARROW_HEIGHT = 14;
    private static final int ARROW_WIDTH = 12;

    private final NonNullList<Slot> slots;

    public CrystalFurnaceScreen(CrystalFurnaceContainer container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.slots = container.slots;

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.renderSlots(poseStack);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    private void renderSlots(PoseStack poseStack) {
        for (Slot slot: this.slots) {
            if (slot.isActive()) {
                this.blit(poseStack, slot.x - 1 + this.leftPos, slot.y -1 + this.topPos, 176, 27, 18, 18);
                if (slot instanceof CrystalFurnaceOutputSlot) {
                    // Draw arrow
                    this.blit(poseStack, slot.x + 3 + this.leftPos, slot.y + 18 + this.topPos, 188, 13, 11, 14);
                    // Draw arrow progress
                    float progress = this.menu.getBurnProgress(slot.index - 5);
                    int height = (int) (progress * ARROW_HEIGHT);
                    this.blit(poseStack, slot.x + 3 + this.leftPos, slot.y + 18 + this.topPos + ARROW_HEIGHT - height, 176, 13 + ARROW_HEIGHT - height, ARROW_WIDTH, height);
                } else if (slot instanceof CrystalFurnaceInputSlot) {
                    // Draw fire below
                    this.blit(poseStack, slot.x + 1 + this.leftPos, slot.y + 18 + this.topPos, 189, 0, 14, 13);
                    // Draw lit progress
                    if (this.menu.isLit()) {
                        float litProgress = this.menu.getLitProgress();
                        int height = (int) (litProgress * 13);
                        this.blit(poseStack, slot.x + 2 + this.leftPos, slot.y + 18 + this.topPos + 13 - height, 176, 13 - height, 14, height);
                    }
                } else if (slot instanceof  CrystalFurnaceFuelSlot && slot.index != 10) {
                    // Draw fuel arrow thing
                    this.blit(poseStack, slot.x + 4 + this.leftPos, slot.y + 19 + this.topPos, 176, 45, 8, 4);
                }
            }
        }
    }
}
