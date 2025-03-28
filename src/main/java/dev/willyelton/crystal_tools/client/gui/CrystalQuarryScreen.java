package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.EnergyBarWidget;
import dev.willyelton.crystal_tools.client.gui.component.backpack.BackpackScreenButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.OpenContainerPayload;
import dev.willyelton.crystal_tools.utils.IntegerUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryScreen extends AbstractContainerScreen<CrystalQuarryContainerMenu> implements SubScreenContainerScreen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_quarry.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;

    private final float expLabelX;
    private BackpackScreenButton skillTreeButton;

    public CrystalQuarryScreen(CrystalQuarryContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 227;
        this.inventoryLabelY = this.imageHeight - 97;
        this.expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
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
                Component.literal(String.format("Using %s FE/Tick", this.menu.getEnergyCost())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6,
                4210752, false);

        BlockPos miningAt = this.menu.getMiningAt();
        guiGraphics.drawString(this.font,
                Component.literal(String.format("Mining At %d, %d, %d", miningAt.getX(), miningAt.getY(), miningAt.getZ())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6 + 10,
                4210752, false);

        this.skillTreeButton.setBadgeCounter(this.menu.getSkillPoints());
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                new EnergyBarWidget(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, Component.empty(), this.font, this.menu)
        );
        int screenButtonY = this.topPos + 22;

        skillTreeButton = this.addRenderableWidget(new BackpackScreenButton(this.leftPos - 21, screenButtonY, Component.literal("Open Skill Tree"),
                button -> {
                    ModGUIs.openScreen(new BlockEntityUpgradeScreen(this.menu, menu.getPlayer(), () -> PacketDistributor.sendToServer(new OpenContainerPayload(this.menu.getBlockPos().asLong()))));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Open Skill Tree");
                    guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(CrystalQuarryScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                }, 40));
        screenButtonY += 21;

        List<BackpackScreenButton> subScreenButtons = getSideButtons(this.leftPos - 21, screenButtonY, this.width, menu);
        subScreenButtons.forEach(this::addRenderableWidget);
    }

    @Override
    public List<BackpackSubScreen<?, ?>> getSubScreens() {
        List<BackpackSubScreen<?, ?>> subScreens = new ArrayList<>();

        if (this.menu.getFilterRows() > 0) {
            subScreens.add(new FilterConfigScreen<>(menu, menu.getPlayer().getInventory(), this, Component.literal("Trash Filter")) {
                @Override
                public Component getButtonName() {
                    return Component.literal("Configure Trash Filters");
                }
            });
        }

        subScreens.add(new QuarrySettingsScreen(menu, menu.getPlayer().getInventory(), this));

        return subScreens;
    }
}
