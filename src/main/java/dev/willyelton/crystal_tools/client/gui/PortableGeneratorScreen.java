package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.inventory.container.AbstractGeneratorContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.SLOTS_PER_ROW;
import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.START_X;
import static dev.willyelton.crystal_tools.common.inventory.container.PortableGeneratorContainerMenu.START_Y;

public class PortableGeneratorScreen extends CrystalGeneratorScreen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/portable_generator.png");
    private static final int DISABLED_SLOT_TEXTURE_X = 176;
    private static final int DISABLED_SLOT_TEXTURE_Y = 13;
    private static final int DISABLED_SLOT_TEXTURE_SIZE = 18;
    private static final int TOTAL_SLOTS = 27;

    private final int activeSlots;
    private final ItemStack generatorStack;

    public PortableGeneratorScreen(AbstractGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 218;
        this.inventoryLabelY = this.imageHeight - 94;

        if (container instanceof PortableGeneratorContainerMenu portableGeneratorContainerMenu) {
            this.activeSlots = portableGeneratorContainerMenu.getActiveSlots();
            this.generatorStack = portableGeneratorContainerMenu.getGeneratorStack();
        } else {
            this.activeSlots = 0;
            this.generatorStack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        this.renderDisabledSlots(guiGraphics);
    }

    @Override
    protected Screen upgradeButtonScreen() {
        return new UpgradeScreen(generatorStack, menu.getPlayer());
    }

    @Override
    protected ResourceLocation texture() {
        return TEXTURE;
    }

    @Override
    protected int fireY() {
        return 108;
    }

    protected void renderDisabledSlots(GuiGraphics guiGraphics) {
        for (int i = activeSlots; i < TOTAL_SLOTS; i++) {
            int x = i % SLOTS_PER_ROW;
            int y = i / SLOTS_PER_ROW;
            guiGraphics.blit(texture(), leftPos + START_X + x * DISABLED_SLOT_TEXTURE_SIZE - 1, topPos + START_Y + y * DISABLED_SLOT_TEXTURE_SIZE - 1, DISABLED_SLOT_TEXTURE_X, DISABLED_SLOT_TEXTURE_Y, DISABLED_SLOT_TEXTURE_SIZE, DISABLED_SLOT_TEXTURE_SIZE, 256, 256);
        }
    }
}
