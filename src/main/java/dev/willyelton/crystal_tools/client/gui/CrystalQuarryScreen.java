package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.component.EnergyBarWidget;
import dev.willyelton.crystal_tools.client.gui.component.backpack.BackpackScreenButton;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalQuarryContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.network.data.OpenContainerPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryScreen extends BaseMenuUpgradeScreen<CrystalQuarryContainerMenu> implements SubScreenContainerScreen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_quarry.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;

    private BackpackScreenButton skillTreeButton;

    public CrystalQuarryScreen(CrystalQuarryContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_quarry"));

        this.imageHeight = 227;
        this.inventoryLabelY = this.imageHeight - 97;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
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
                Component.literal(String.format("Using %s FE/Tick", this.menu.getEnergyCost())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6,
                4210752 + 0xFF000000, false);

        BlockPos miningAt = this.menu.getMiningAt();
        guiGraphics.drawString(this.font,
                Component.literal(String.format("Mining At %d, %d, %d", miningAt.getX(), miningAt.getY(), miningAt.getZ())),
                this.inventoryLabelX,
                ENERGY_Y + ENERGY_HEIGHT + 6 + 10,
                4210752 + 0xFF000000, false);

        this.skillTreeButton.setBadgeCounter(this.menu.getSkillPoints());
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                new EnergyBarWidget(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, Component.empty(), this.font, this.menu)
        );
        int screenButtonY = this.topPos + 22 + 21;

        List<BackpackScreenButton> subScreenButtons = getSideButtons(this.leftPos - 21, screenButtonY, this.width, menu);
        subScreenButtons.forEach(this::addRenderableWidget);
    }

    @Override
    protected void initUpgradeButton(Holder.Reference<SkillData> skillData) {
        skillTreeButton = this.addRenderableWidget(new BackpackScreenButton(this.leftPos - 21, this.topPos + 22, Component.literal("Open Skill Tree"),
                button -> {
                    ModGUIs.openScreen(new BlockEntityUpgradeScreen(this.menu, menu.getPlayer(), () -> ClientPacketDistributor.sendToServer(new OpenContainerPayload(this.menu.getBlockPos().asLong())),
                            skillData.value(), skillData.key()));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Open Skill Tree");
                    guiGraphics.setTooltipForNextFrame(this.font, this.font.split(textComponent, Math.max(CrystalQuarryScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                }, 40));
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
