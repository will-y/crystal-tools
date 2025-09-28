package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.BlockEntityUpgradeButton;
import dev.willyelton.crystal_tools.client.gui.component.EnergyBarWidget;
import dev.willyelton.crystal_tools.common.inventory.container.AbstractGeneratorContainerMenu;
import dev.willyelton.crystal_tools.utils.IntegerUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// TODO: Superclass for this and furnace could be useful. Drawing fire + animations + upgrade button + skill point bar, abstract the "bar" component?
public class CrystalGeneratorScreen extends AbstractContainerScreen<AbstractGeneratorContainerMenu> {
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

    protected static final int UPGRADE_BUTTON_X = 155;
    protected static final int UPGRADE_BUTTON_Y = 6;
    protected static final int UPGRADE_BUTTON_WIDTH = 12;
    protected static final int UPGRADE_BUTTON_HEIGHT = 12;

    private final float expLabelX;

    public CrystalGeneratorScreen(AbstractGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
        this.expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture());
        guiGraphics.blit(texture(), leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
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
                new BlockEntityUpgradeButton(UPGRADE_BUTTON_X + this.leftPos,
                        UPGRADE_BUTTON_Y + this.topPos,
                        UPGRADE_BUTTON_WIDTH,
                        UPGRADE_BUTTON_HEIGHT,
                        Component.literal("+"),
                        pButton -> {
                            Screen toOpen = upgradeButtonScreen();
                            if (toOpen == null) {
                                CrystalTools.LOGGER.warn("Couldn't open upgrade button, no skill tree defined");
                            } else {
                                ModGUIs.openScreen(toOpen);
                            }
                        },
                        (button, guiGraphics, mouseX, mouseY) -> {
                            Component textComponent = Component.literal(this.menu.getSkillPoints() + " Point(s) Available");
                            guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalGeneratorScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                        },
                        false));

        this.addRenderableWidget(
                new EnergyBarWidget(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, Component.empty(), this.font, this.menu)
        );
    }

    protected Screen upgradeButtonScreen() {
        return new BlockEntityUpgradeScreen(this.menu, this.menu.getPlayer(), this);
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
        guiGraphics.blit(TEXTURE, this.leftPos + fireX(), this.topPos + fireY(), FIRE_TEXTURE_OFF_X, FIRE_TEXTURE_Y, FIRE_WIDTH, FIRE_HEIGHT);

        if (this.menu.isLit()) {
            float litProgress = this.menu.getLitProgress();
            int height = (int) (litProgress * FIRE_HEIGHT);
            guiGraphics.blit(TEXTURE, this.leftPos + fireX(), this.topPos + fireY() + FIRE_HEIGHT - height, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_HEIGHT - height, FIRE_WIDTH, height);
        }
    }
}
