package dev.willyelton.crystal_tools.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.item.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.item.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SkillButton extends Button {
    public static final ResourceLocation SKILL_BUTTON_LOCATION = new ResourceLocation("crystal_tools", "textures/gui/skill_button.png");
    private static final int ITEM_WIDTH = 16;
    // used when it is completed, should also set not active but render differently
    public boolean isComplete = false;
    private final SkillDataNode dataNode;
    public int xOffset = 0;
    public int yOffset = 0;
    private final Player player;
    private final SkillData data;
    private final List<SkillItemRequirement> items;
    private final ItemRenderer itemRenderer;

    private final List<int[]> itemPositions;

    public SkillButton(int x, int y, int width, int height, Component name, OnPress onPress, OnTooltip onTooltip, SkillData data, SkillDataNode node, Player player) {
        super(x, y, width, height, name, onPress, onTooltip);
        this.dataNode = node;
        this.data = data;
        this.player = player;
        this.items = node.getRequirements().stream()
                .filter(requirement -> requirement.getRequirementType() == RequirementType.ITEM)
                .map(skillDataRequirement -> (SkillItemRequirement) skillDataRequirement)
                .collect(Collectors.toList());

        this.itemRenderer = Minecraft.getInstance().getItemRenderer();

        this.itemPositions = new ArrayList<>();

        itemPositions.add(new int[] {-ITEM_WIDTH / 2, -ITEM_WIDTH / 2});
        itemPositions.add(new int[] {width - ITEM_WIDTH / 2, -ITEM_WIDTH / 2});
        itemPositions.add(new int[] {width - ITEM_WIDTH  / 2, height - ITEM_WIDTH / 2});
        itemPositions.add(new int[] {-ITEM_WIDTH / 2, height - ITEM_WIDTH / 2});
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = pMouseX >= this.x + xOffset && pMouseY >= this.y + yOffset && pMouseX < this.x + xOffset + this.width && pMouseY < this.y + yOffset + this.height;
            this.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            if (!this.isComplete)
                this.renderItems(pPoseStack);
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SKILL_BUTTON_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        // need to override this based on my button texture
        int i = this.getTextureY(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        // first half of button
        this.blit(poseStack, this.x + xOffset, this.y + yOffset, 0, i * 20, this.width / 2, this.height);
        // second half of button
        this.blit(poseStack, this.x + this.width / 2 + xOffset, this.y + yOffset, 200 - this.width / 2, i * 20, this.width / 2, this.height);
        // pretty sure does nothing
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        // might need to change based off of how text looks on colors I pick
        int j = getFGColor();
        drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2 + this.xOffset, this.y + (this.height - 8) / 2 + this.yOffset, j | Mth.ceil(this.alpha * 255.0F) << 24);

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public int getTextureY(boolean hovered) {
        if (this.isComplete) {
            return 3;
        } else if (!this.isActive()) {
            return 0;
        } else if (hovered) {
            return 2;
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
    protected boolean clicked(double pMouseX, double pMouseY) {
        return this.active && this.visible && pMouseX >= (double) (this.x + xOffset) && pMouseY >= (double) (this.y + yOffset) && pMouseX < (double)(this.x + this.width + xOffset) && pMouseY < (double)(this.y + this.height + yOffset);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return this.active && this.visible && pMouseX >= (double) (this.x + xOffset) && pMouseY >= (double) (this.y + yOffset) && pMouseX < (double)(this.x + this.width + xOffset) && pMouseY < (double)(this.y + this.height + yOffset);
    }

    private void renderItem(PoseStack poseStack, ItemStack itemStack, int x, int y, boolean canLevel) {
        this.itemRenderer.renderGuiItem(itemStack, x, y);
        int color;
        if (canLevel) {
            color = Colors.fromRGB(0, 255, 0, 50);
        } else {
            color = Colors.fromRGB(255, 0, 0, 50);
        }
        fill(poseStack, x, y, x + 16, y + 16, color);
    }

    private void renderItems(PoseStack poseStack) {
        int i = 0;
        for (SkillItemRequirement req : this.items) {
            for (Item item : req.getItems()) {
                this.renderItem(poseStack, item.getDefaultInstance(), this.x + this.xOffset + this.itemPositions.get(i)[0], this.y + this.yOffset + this.itemPositions.get(i)[1], req.hasItem(this.player, item));
                i++;
                if (i > 3) break;
            }
        }
    }
}
