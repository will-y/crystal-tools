package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.EnergyBarWidget;
import dev.willyelton.crystal_tools.common.inventory.container.AbstractGeneratorContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrystalGeneratorScreen extends BaseMenuUpgradeScreen<AbstractGeneratorContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_generator.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;

    private static final int FIRE_X = 82;
    private static final int FIRE_Y = 78;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 13;
    private static final int FIRE_TEXTURE_ON_X = 176;
    private static final int FIRE_TEXTURE_OFF_X = 190;
    private static final int FIRE_TEXTURE_Y = 0;

    public CrystalGeneratorScreen(AbstractGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_generator"));

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture(), leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.renderFire(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        guiGraphics.drawString(this.font,
                Component.literal(String.format("Generating %s FE/Tick", this.menu.getCurrentGeneration())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6,
                4210752 + 0xFF000000, false);
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                new EnergyBarWidget(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, Component.empty(), this.font, this.menu)
        );
    }

    protected ResourceLocation texture() {
        return TEXTURE;
    }

    protected int fireX() {
        return FIRE_X;
    }

    protected int fireY() {
        return FIRE_Y;
    }

    private void renderFire(GuiGraphics guiGraphics) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture(), this.leftPos + fireX(), this.topPos + fireY(), FIRE_TEXTURE_OFF_X, FIRE_TEXTURE_Y, FIRE_WIDTH, FIRE_HEIGHT, 256, 256);

        if (this.menu.isLit()) {
            float litProgress = this.menu.getLitProgress();
            int height = (int) (litProgress * FIRE_HEIGHT);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture(), this.leftPos + fireX(), this.topPos + fireY() + FIRE_HEIGHT - height, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_HEIGHT - height, FIRE_WIDTH, height, 256, 256);
        }
    }
}
