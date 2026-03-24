package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.EnergyBarWidget;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryScreen extends BaseMenuUpgradeScreen<CrystalQuarryContainerMenu> implements SubScreenContainerScreen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_quarry.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;

    public CrystalQuarryScreen(CrystalQuarryContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title, Identifier.fromNamespaceAndPath(CrystalTools.MODID, "crystal_quarry"));

        this.imageHeight = 227;
        this.inventoryLabelY = this.imageHeight - 97;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractLabels(guiGraphics, mouseX, mouseY);

        guiGraphics.text(this.font,
                Component.literal(String.format("Using %s FE/Tick", this.menu.getEnergyCost())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6,
                4210752 + 0xFF000000, false);

        BlockPos miningAt = this.menu.getMiningAt();
        guiGraphics.text(this.font,
                Component.literal(String.format("Mining At %d, %d, %d", miningAt.getX(), miningAt.getY(), miningAt.getZ())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6 + 10,
                4210752 + 0xFF000000, false);
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                new EnergyBarWidget(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, Component.empty(), this.font, this.menu)
        );
    }

    @Override
    public List<ContainerSubScreen<?, ?>> getSubScreens() {
        List<ContainerSubScreen<?, ?>> subScreens = new ArrayList<>();

        if (this.menu.getFilterRows() > 0) {
            subScreens.add(new FilterConfigScreen<>(menu, menu.getPlayer().getInventory(), this, Component.literal("Trash Filter")) {
                @Override
                public Component getButtonName() {
                    return Component.translatable("button.crystal_tools.trash_filter_settings");
                }
            });
        }

        subScreens.add(new QuarrySettingsScreen(menu, menu.getPlayer().getInventory(), this));
        subScreens.add(new SideConfigScreen<>(menu, menu.getPlayer().getInventory(), this));

        return subScreens;
    }
}
