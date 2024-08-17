package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.FurnaceUpgradeButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import dev.willyelton.crystal_tools.utils.IntegerUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// TODO: Superclass for this and furnace could be useful. Drawing fire + animations + upgrade button + skill point bar, abstract the "bar" component?
public class CrystalGeneratorScreen extends AbstractContainerScreen<CrystalGeneratorContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_generator.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;
    private static final int ENERGY_TEXTURE_X = 0;
    private static final int ENERGY_TEXTURE_Y = 191;

    private static final int FIRE_X = 82;
    private static final int FIRE_Y = 78;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 13;
    private static final int FIRE_TEXTURE_ON_X = 176;
    private static final int FIRE_TEXTURE_OFF_X = 190;
    private static final int FIRE_TEXTURE_Y = 0;

    private static final int UPGRADE_BUTTON_X = 155;
    private static final int UPGRADE_BUTTON_Y = 6;
    private static final int UPGRADE_BUTTON_WIDTH = 12;
    private static final int UPGRADE_BUTTON_HEIGHT = 12;

    private final float expLabelX;

    public CrystalGeneratorScreen(CrystalGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
        expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderEnergyBar(guiGraphics);
        this.renderFire(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        // TODO: Abstract the energy bar component?
        int x1 = this.leftPos + ENERGY_X;
        int x2 = x1 + ENERGY_WIDTH;
        int y1 = this.topPos + ENERGY_Y;
        int y2 = y1 + ENERGY_HEIGHT;

        if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
            String tooltipString = String.format("%d/%d FE", (int) menu.getCurrentEnergy(), (int) menu.getMaxEnergy());
            guiGraphics.renderTooltip(this.font, Component.literal(tooltipString), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int xOffset = (IntegerUtils.getDigits(this.menu.getExp()) - 1) * 6 + (IntegerUtils.getDigits(this.menu.getExpCap()) - 2) * 6;
        guiGraphics.drawString(this.font,
                Component.literal(String.format("Exp: %d/%d", this.menu.getExp(), this.menu.getExpCap())),
                (int) (this.expLabelX - xOffset),
                this.inventoryLabelY,
                4210752, false);

        guiGraphics.drawString(this.font,
                Component.literal(String.format("Generating %s FE/Tick", this.menu.getCurrentGeneration())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6,
                4210752, false);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(
                new FurnaceUpgradeButton(UPGRADE_BUTTON_X + this.leftPos,
                        UPGRADE_BUTTON_Y + this.topPos,
                        UPGRADE_BUTTON_WIDTH,
                        UPGRADE_BUTTON_HEIGHT,
                        Component.literal("+"),
                        pButton -> ModGUIs.openScreen(new BlockEntityUpgradeScreen(this.menu, this.menu.getPlayer(), this)),
                        (button, guiGraphics, mouseX, mouseY) -> {
                            Component textComponent = Component.literal(this.menu.getSkillPoints() + " Point(s) Available");
                            guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalGeneratorScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                        },
                        false));
    }

    private void renderEnergyBar(GuiGraphics guiGraphics) {
        float energyProgress = this.menu.getCurrentEnergy() / this.menu.getMaxEnergy();
        int width = (int) (energyProgress * ENERGY_WIDTH);
        guiGraphics.blit(TEXTURE, this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_TEXTURE_X, ENERGY_TEXTURE_Y, width, ENERGY_HEIGHT);
    }

    private void renderFire(GuiGraphics guiGraphics) {
        guiGraphics.blit(TEXTURE, this.leftPos + FIRE_X, this.topPos + FIRE_Y, FIRE_TEXTURE_OFF_X, FIRE_TEXTURE_Y, FIRE_WIDTH, FIRE_HEIGHT);

        if (this.menu.isLit()) {
            float litProgress = this.menu.getLitProgress();
            int height = (int) (litProgress * FIRE_HEIGHT);
            guiGraphics.blit(TEXTURE, this.leftPos + FIRE_X, this.topPos + FIRE_Y + FIRE_HEIGHT - height, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_HEIGHT - height, FIRE_WIDTH, height);
        }
    }
}
