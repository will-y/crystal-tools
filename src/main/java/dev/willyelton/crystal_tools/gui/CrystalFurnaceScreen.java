package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.gui.component.FurnaceUpgradeButton;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceFuelSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceInputSlot;
import dev.willyelton.crystal_tools.levelable.block.container.slot.CrystalFurnaceOutputSlot;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.IntegerUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceScreen extends AbstractContainerScreen<CrystalFurnaceContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/crystal_furnace.png");
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

    private static final int UPGRADE_BUTTON_X = 155;
    private static final int UPGRADE_BUTTON_Y = 6;
    private static final int UPGRADE_BUTTON_WIDTH = 12;
    private static final int UPGRADE_BUTTON_HEIGHT = 12;

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

    private final NonNullList<Slot> slots;
    private final CrystalFurnaceBlockEntity blockEntity;
    private FurnaceUpgradeButton upgradeButton;
    private final float expLabelX;

    private int counter = 0;
    private int maxCounter = 10;
    private int animFrame = 0;
    private int maxAnimFrame = 3;

    public CrystalFurnaceScreen(CrystalFurnaceContainer container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.slots = container.slots;
        this.blockEntity = container.getBlockEntity();

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
        expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.renderSlots(poseStack);
        this.renderFuelBar(poseStack);
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
                this.blit(poseStack, slot.x - 1 + this.leftPos, slot.y -1 + this.topPos, SLOT_TEXTURE_X, SLOT_TEXTURE_Y, SLOT_TEXTURE_SIZE, SLOT_TEXTURE_SIZE);
                if (slot instanceof CrystalFurnaceOutputSlot) {
                    // Draw arrow
                    this.blit(poseStack, slot.x + 3 + this.leftPos, slot.y + 18 + this.topPos, ARROW_TEXTURE_X, ARROW_TEXTURE_Y, ARROW_WIDTH, ARROW_HEIGHT);
                    // Draw arrow progress
                    float progress = this.menu.getBurnProgress(slot.index - 5);
                    int height = (int) (progress * ARROW_HEIGHT);
                    this.blit(poseStack, slot.x + 3 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + ARROW_HEIGHT - height, ARROW_TEXTURE_ON_X, ARROW_TEXTURE_Y + ARROW_HEIGHT - height, ARROW_WIDTH, height);
                } else if (slot instanceof CrystalFurnaceInputSlot) {
                    // Draw fire below
                    this.blit(poseStack, slot.x + 1 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + 2, FIRE_TEXTURE_X, FIRE_TEXTURE_Y, FIRE_TEXTURE_WIDTH, FIRE_TEXTURE_HEIGHT);
                    // Draw lit progress
                    if (this.menu.isLit()) {
                        float litProgress = this.menu.getLitProgress();
                        int height = (int) (litProgress * 13);
                        this.blit(poseStack, slot.x + 2 + this.leftPos, slot.y + SLOT_TEXTURE_SIZE + this.topPos + FIRE_TEXTURE_HEIGHT - height + 2, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_TEXTURE_HEIGHT - height, FIRE_TEXTURE_WIDTH, height);
                    }
                } else if (slot instanceof CrystalFurnaceFuelSlot && slot.index != 10) {
                    // Draw fuel arrow thing
                    this.blit(poseStack, slot.x + 4 + this.leftPos, slot.y + 19 + this.topPos, FUEL_ARROW_TEXTURE_X, FUEL_ARROW_TEXTURE_Y, FUEL_ARROW_TEXTURE_WIDTH, FUEL_ARROW_TEXTURE_HEIGHT);
                }
            }
        }
    }

    private void renderFuelBar(PoseStack poseStack) {
        if (counter > maxCounter) {
            counter = 0;
            this.animFrame += 1;
            if (this.animFrame > this.maxAnimFrame) this.animFrame = 0;
        }
        counter++;

        int widthIncrease = (this.menu.getNumActiveSlots() - 1) * FUEL_BAR_WIDTH_INCREASE;
        this.blit(poseStack, this.leftPos + FUEL_BAR_X, this.topPos + FUEL_BAR_Y, FUEL_BAR_TEXTURE_X, FUEL_BAR_TEXTURE_Y, FUEL_BAR_INITIAL_WIDTH + widthIncrease, FUEL_BAR_HEIGHT);

        if (this.menu.isLit()) {
            // draw animated fuel bar
            this.blit(poseStack, this.leftPos + FUEL_BAR_X, this.topPos + FUEL_BAR_Y, FUEL_BAR_TEXTURE_X, FUEL_BAR_TEXTURE_Y + (this.animFrame + 1) * 2, FUEL_BAR_INITIAL_WIDTH + widthIncrease, FUEL_BAR_HEIGHT);

            // also animate the inserter things
            this.blit(poseStack, this.leftPos + FUEL_INSERT_X, this.topPos + FUEL_INSERT_Y, FUEL_INSERT_TEXTURE_X + this.animFrame * FUEL_INSERT_WIDTH, FUEL_INSERT_TEXTURE_Y, FUEL_INSERT_WIDTH, FUEL_INSERT_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        int xOffset = (IntegerUtils.getDigits(this.menu.getExp()) - 1) * 6 + (IntegerUtils.getDigits(this.menu.getExpCap()) - 2) * 6;
        this.font.draw(poseStack,
                Component.literal(String.format("Exp: %d/%d", this.menu.getExp(), this.menu.getExpCap())),
                this.expLabelX - xOffset,
                (float)this.inventoryLabelY, 4210752);
    }

    @Override
    protected void init() {
        super.init();
        this.upgradeButton = this.addRenderableWidget(
                new FurnaceUpgradeButton(UPGRADE_BUTTON_X + this.leftPos,
                        UPGRADE_BUTTON_Y + this.topPos,
                        UPGRADE_BUTTON_WIDTH,
                        UPGRADE_BUTTON_HEIGHT,
                        Component.literal("+"),
                        pButton -> ModGUIs.openScreen(new FurnaceUpgradeScreen(this.menu, this.menu.getPlayer(), this)),
                        (button, poseStack, mouseX, mouseY) -> {
                            Component textComponent = Component.literal(this.menu.getSkillPoints() + " Points Available");
                            CrystalFurnaceScreen.this.renderTooltip(poseStack, CrystalFurnaceScreen.this.minecraft.font.split(textComponent, Math.max(CrystalFurnaceScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                        },
                        false));
    }
}
