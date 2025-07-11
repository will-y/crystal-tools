package dev.willyelton.crystal_tools.client.gui.component;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SkillButton extends CrystalToolsButton {
    public static final ResourceLocation SKILL_BUTTON_LOCATION = ResourceLocation.fromNamespaceAndPath("crystal_tools", "textures/gui/skill_button.png");
    private static final int ITEM_WIDTH = 16;
    // used when it is completed, should also set not active but render differently
    public boolean isComplete = false;
    private final SkillDataNode dataNode;
    public int xOffset = 0;
    public int yOffset = 0;
    private final Player player;
    private final List<SkillItemRequirement> items;
    private final SkillPoints skillPoints;

    private final List<int[]> itemPositions;

    public SkillButton(int x, int y, int width, int height, Component name, OnPress onPress,
                       CrystalToolsButton.OnTooltip onTooltip, SkillDataNode node,
                       Player player, SkillPoints skillPoints) {
        super(x, y, width, height, name, onPress, onTooltip);
        this.dataNode = node;
        this.player = player;
        this.items = node.getRequirements().stream()
                .filter(requirement -> requirement.getRequirementType() == RequirementType.ITEM)
                .map(skillDataRequirement -> (SkillItemRequirement) skillDataRequirement)
                .collect(Collectors.toList());
        this.skillPoints = skillPoints;
        this.itemPositions = new ArrayList<>();

        itemPositions.add(new int[] {-ITEM_WIDTH / 2, -ITEM_WIDTH / 2});
        itemPositions.add(new int[] {width - ITEM_WIDTH / 2, -ITEM_WIDTH / 2});
        itemPositions.add(new int[] {width - ITEM_WIDTH  / 2, height - ITEM_WIDTH / 2});
        itemPositions.add(new int[] {-ITEM_WIDTH / 2, height - ITEM_WIDTH / 2});
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = pMouseX >= this.getX() + xOffset && pMouseY >= this.getY() + yOffset && pMouseX < this.getX() + xOffset + this.width && pMouseY < this.getY() + yOffset + this.height;
            super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            if (CrystalToolsConfig.ENABLE_ITEM_REQUIREMENTS.get() && !this.isComplete)
                this.renderItems(guiGraphics);
        }
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        // first half of button
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SKILL_BUTTON_LOCATION, this.getX() + xOffset, this.getY() + yOffset, 0, textureY * 20, this.width / 2, this.height, 256, 256);
        // second half of button
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SKILL_BUTTON_LOCATION, this.getX() + this.width / 2 + xOffset, this.getY() + yOffset, 200 - this.width / 2, textureY * 20, this.width / 2, this.height, 256, 256);
    }

    @Override
    protected void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor) {
        guiGraphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2 + this.xOffset, this.getY() + (this.height - 8) / 2 + this.yOffset, fgColor | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public int getTextureY(boolean hovered) {
        boolean isInfinite = this.dataNode.getLimit() != 1;
        int points = this.skillPoints.getPoints(this.dataNode.getId());
        if (isInfinite && points > 0) {
            if (!this.isActive()) {
                return 3;
            } else if (hovered) {
                return 6;
            } else {
                return 5;
            }
        } else {
            if (this.isComplete) {
                return 3;
            } else if (!this.isActive()) {
                return 0;
            } else if (hovered) {
                return 2;
            }
        }

        return 1;
    }

    public void setComplete() {
        this.isComplete = true;
        this.active = false;
    }

    public SkillDataNode getDataNode() {
        return this.dataNode;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return this.active &&
                this.visible &&
                pMouseX >= (double) (this.getX() + xOffset) &&
                pMouseY >= (double) (this.getY() + yOffset) &&
                pMouseX < (double)(this.getX() + this.width + xOffset) &&
                pMouseY < (double)(this.getY() + this.height + yOffset);
    }

    private void renderItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, boolean canLevel) {
        guiGraphics.renderItem(itemStack, x, y);
        int color;
        if (canLevel) {
            color = Colors.fromRGB(0, 255, 0, 50);
        } else {
            color = Colors.fromRGB(255, 0, 0, 50);
        }
        guiGraphics.fill(x, y, x + 16, y + 16, color);
    }

    private void renderItems(GuiGraphics guiGraphics) {
        int i = 0;
        for (SkillItemRequirement req : this.items) {
            for (Item item : req.getItems()) {
                this.renderItem(guiGraphics, item.getDefaultInstance(), this.getX() + this.xOffset + this.itemPositions.get(i)[0], this.getY() + this.yOffset + this.itemPositions.get(i)[1], req.hasItem(this.player, item));
                i++;
                if (i > 3) break;
            }
        }
    }
}
