package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.client.gui.component.BlockEntityUpgradeButton;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.IntegerUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class BaseMenuUpgradeScreen<T extends LevelableContainerMenu> extends AbstractContainerScreen<T> {

    protected static final int UPGRADE_BUTTON_X = 155;
    protected static final int UPGRADE_BUTTON_Y = 6;
    protected static final int UPGRADE_BUTTON_WIDTH = 12;
    protected static final int UPGRADE_BUTTON_HEIGHT = 12;

    private final ResourceLocation skillLocation;
    private final float expLabelX;

    public BaseMenuUpgradeScreen(T menu, Inventory playerInventory, Component title, ResourceLocation skillLocation) {
        super(menu, playerInventory, title);

        this.skillLocation = skillLocation;
        this.expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void init() {
        super.init();

        Optional<Holder.Reference<SkillData>> optionalSkillData = getSkillData();
        optionalSkillData.ifPresent(this::initUpgradeButton);
    }

    protected Optional<Holder.Reference<SkillData>> getSkillData() {
        Level level = this.getMenu().getBlockEntity().getLevel();

        if (level != null) {
            Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS);
            if (skillDataOptional.isPresent()) {
                Registry<SkillData> skillDataRegistry = skillDataOptional.get();

                return skillDataRegistry.get(skillLocation);
            }
        }

        return Optional.empty();
    }

    protected void initUpgradeButton(Holder.Reference<SkillData> skillData) {
        this.addRenderableWidget(
                new BlockEntityUpgradeButton(UPGRADE_BUTTON_X + this.leftPos,
                        UPGRADE_BUTTON_Y + this.topPos,
                        UPGRADE_BUTTON_WIDTH,
                        UPGRADE_BUTTON_HEIGHT,
                        Component.literal("+"),
                        pButton -> ModGUIs.openScreen(new BlockEntityUpgradeScreen(this.menu, this.menu.getPlayer(), this, skillData.value(), skillData.key())),
                        (button, guiGraphics, mouseX, mouseY) -> {
                            Component textComponent = Component.literal(this.menu.getSkillPoints() + " Point(s) Available");
                            guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(BaseMenuUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                        },
                        false));
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
    }
}
