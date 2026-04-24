package dev.willyelton.crystal_tools.api.client.gui;

import dev.willyelton.crystal_tools.api.client.gui.component.ContainerSideButton;
import dev.willyelton.crystal_tools.api.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.api.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.api.common.network.payload.OpenContainerPayload;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import dev.willyelton.crystal_tools.api.utils.IntegerUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;
import java.util.Optional;

import static dev.willyelton.crystal_tools.api.utils.ScreenUtils.openScreen;

public abstract class BaseMenuUpgradeScreen<T extends LevelableContainerMenu> extends AbstractContainerScreen<T> implements SubScreenContainerScreen {

    private final Identifier skillLocation;
    private final float expLabelX;
    private ContainerSideButton skillTreeButton;

    public BaseMenuUpgradeScreen(T menu, Inventory playerInventory, Component title, Identifier skillLocation) {
        super(menu, playerInventory, title);

        this.skillLocation = skillLocation;
        this.expLabelX = this.inventoryLabelX + 112;
    }

    @Override
    protected void init() {
        super.init();

        Optional<Holder.Reference<SkillData>> optionalSkillData = getSkillData();
        optionalSkillData.ifPresent(this::initUpgradeButton);

        List<ContainerSideButton> subScreenButtons = getSideButtons(this.leftPos - 21, this.topPos + 43, this.width, menu);
        subScreenButtons.forEach(this::addRenderableWidget);
    }

    protected Optional<Holder.Reference<SkillData>> getSkillData() {
        Level level = this.getMenu().getLevel();

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
        skillTreeButton = this.addRenderableWidget(new ContainerSideButton(this.leftPos - 21, this.topPos + 22, Component.literal("Open Skill Tree"),
                button -> {
                    openScreen(new BlockEntityUpgradeScreen(this.menu, menu.getPlayer(), () -> ClientPacketDistributor.sendToServer(new OpenContainerPayload(this.menu.getBlockPos().asLong())),
                            skillData.value(), skillData.key()));
                },
                (button, guiGraphics, mouseX, mouseY) -> {
                    Component textComponent = Component.literal("Open Skill Tree");
                    guiGraphics.setTooltipForNextFrame(this.font, this.font.split(textComponent, Math.max(BaseMenuUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
                }, 40));
    }

    protected Screen upgradeButtonScreen(Holder.Reference<SkillData> skillData) {
        return new BlockEntityUpgradeScreen(this.menu, this.menu.getPlayer(), this, skillData.value(), skillData.key());
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractLabels(guiGraphics, mouseX, mouseY);
        int xOffset = (IntegerUtils.getDigits(this.menu.getExp()) - 1) * 6 + (IntegerUtils.getDigits(this.menu.getExpCap()) - 2) * 6;
        guiGraphics.text(this.font,
                Component.literal(String.format("Exp: %d/%d", this.menu.getExp(), this.menu.getExpCap())),
                (int) (this.expLabelX - xOffset),
                this.inventoryLabelY,
                4210752 + 0xFF000000, false);

        this.skillTreeButton.setBadgeCounter(this.menu.getSkillPoints());
    }
}
