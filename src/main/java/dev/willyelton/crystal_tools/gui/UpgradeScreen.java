package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.network.ToolHealPacket;
import dev.willyelton.crystal_tools.tool.LevelableItem;
import dev.willyelton.crystal_tools.tool.LevelableTool;
import dev.willyelton.crystal_tools.tool.skill.SkillData;
import dev.willyelton.crystal_tools.tool.skill.SkillDataNode;
import dev.willyelton.crystal_tools.tool.skill.SkillNodeType;
import dev.willyelton.crystal_tools.tool.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.tool.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public class UpgradeScreen extends Screen {
    private final ItemStack tool;
    private final SkillData toolData;
    private final HashMap<Integer, SkillButton> skillButtons = new HashMap<>();
    private Button healButton;

    private static final int Y_PADDING = 20;
    private static final int X_SIZE = 100;
    private static final int Y_SIZE = 20;

    private int xOffset = 0;
    private int yOffset = 0;

    public UpgradeScreen(ItemStack itemStack) {
        super(new TextComponent("Test Title"));
        tool = itemStack;

        int[] points = NBTUtils.getIntArray(tool, "points");
        if (tool.getItem() instanceof LevelableItem) {
            String toolType = ((LevelableItem) tool.getItem()).getItemType();
            toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", toolType)), points);
        } else {
            toolData = null;
        }

    }

    protected void init() {
        List<List<SkillDataNode>> tiers = toolData.getAllNodesByTier();

        int y = Y_PADDING;

        // add skill tree items
        for (List<SkillDataNode> tier : tiers) {
            this.addButtonsFromTier(tier, y);
            y += (Y_PADDING + Y_SIZE);
        }

        // add button to spend skill points to heal tool
        healButton = addRenderableWidget(new Button(5, 15, 30, Y_SIZE, new TextComponent("Heal"), (button) -> {
            PacketHandler.sendToServer(new ToolHealPacket());
            // also do client side to update ui, seems to work, might want to test more
            NBTUtils.addValueToTag(tool, "skill_points", -1);
            this.updateButtons();
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent("Uses a skill point to fully repair this tool");
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }));

        this.updateButtons();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        this.renderBackground(poseStack);
        drawString(poseStack, font, "Skill Points: " + (int) NBTUtils.getFloatOrAddKey(tool, "skill_points"), 5, 5, Colors.TEXT_LIGHT);
//        fill(poseStack, 0, 0, 100, 100, Colors.fromRGB(0, 0, 255));
//        vLine(poseStack, 40, 10, 1000, -16777216);

        super.render(poseStack, mouseX, mouseY, particleTicks);
//        drawDependencyLines(poseStack);
//        drawLine(poseStack, 100, 100, 300, 300, -16777216);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addButtonsFromTier(List<SkillDataNode> nodes, int y) {
        int size = nodes.size();
        int paddingX = (this.width - size * X_SIZE) / (size + 1);

        int x = paddingX;

        for (SkillDataNode node : nodes) {
            this.addButtonFromNode(node, x, y);
            x += (paddingX + X_SIZE);
        }
    }

    private void addButtonFromNode(SkillDataNode node, int x, int y) {
        this.addSkillButton(new SkillButton(x, y, X_SIZE, Y_SIZE, new TextComponent(node.getName()), (button) -> {
            int skillPoints = (int) NBTUtils.getFloatOrAddKey(tool, "skill_points");

            if (skillPoints > 0) {
                NBTUtils.addValueToTag(tool, "skill_points", -1);
                PacketHandler.sendToServer(new ToolAttributePacket("skill_points", -1, -1));
                PacketHandler.sendToServer(new ToolAttributePacket(node.getKey(), node.getValue(), node.getId()));
                node.addPoint();
                if (node.isComplete()) {
                    ((SkillButton) button).setComplete();
                }
                this.updateButtons();
            }
        }, (button, poseStack, mouseX, mouseY) -> {
            String text;
            if (node.getType().equals(SkillNodeType.INFINITE) && node.getPoints() > 0) {
                text = node.getDescription() + "\n" + node.getPoints() + " Points";
            } else {
                text = node.getDescription();
            }

            Component textComponent = new TextComponent(text);
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(textComponent, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, this.toolData, node));
    }

    private void addSkillButton(SkillButton button) {
        this.skillButtons.put(button.getDataNode().getId(), button);
        this.addRenderableWidget(button);
    }

    private void updateButtons() {
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(tool, "skill_points");
        for (SkillButton button : this.skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            button.active = !button.isComplete && node.canLevel(toolData) && skillPoints > 0;
            if (node.isComplete()) {
                button.setComplete();
            }
        }

        this.healButton.active = skillPoints > 0;
    }

    private void drawDependencyLines(PoseStack poseStack) {
        for (SkillButton button : skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            for (SkillDataRequirement requirement : node.getRequirements()) {
                RequirementType type = requirement.getRequirementType();
                int[] nodes = requirement.getRequiredNodes();

                for (int i = 0; i < nodes.length; i++) {
                    int color = Colors.BLACK;
                    switch (type) {
                        case NODE_OR -> {
                        }
                        case NODE_AND -> {
                        }
                        case NODE_NOT -> {
                            // no line for now
                        }
                    }

                    drawLine(poseStack, getButtonBottomCenter(this.skillButtons.get(nodes[i])), getButtonTopCenter(button), color);
                }
            }
        }
    }

    // Scrolling
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.xOffset += dragX;
        this.yOffset += dragY;

        for (SkillButton skillButton : this.skillButtons.values()) {
            skillButton.xOffset = this.xOffset;
            skillButton.yOffset = this.yOffset;
        }

        return false;
    }

    private static void drawLine(PoseStack poseStack, int[] point1, int[] point2, int color) {
        drawLine(poseStack, point1[0], point1[1], point2[0], point2[1], color);
    }

    private static void drawLine(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color) {
        Matrix4f pMatrix = poseStack.last().pose();

        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, (float)maxX, (float)maxY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, (float)minX, (float)minY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, (float)minX - 1, (float)minY + 1, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, (float)maxX - 1, (float)maxY + 1, 0.0F).color(red, green, blue, alpha).endVertex();

        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private int[] getButtonBottomCenter(SkillButton button) {
        int x = button.x + button.xOffset + button.getWidth() / 2;
        int y = button.y + button.yOffset + button.getHeight();

        return new int[] {x, y};
    }

    private int[] getButtonTopCenter(SkillButton button) {
        int x = button.x + button.xOffset + button.getWidth() / 2;
        int y = button.y + button.yOffset;

        return new int[] {x, y};
    }
}
