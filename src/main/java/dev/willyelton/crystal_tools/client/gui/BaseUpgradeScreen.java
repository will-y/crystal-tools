package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.client.gui.component.XpButton;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillNodeType;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataNodeRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.common.network.data.RemoveItemPayload;
import dev.willyelton.crystal_tools.common.network.data.RemoveXpPayload;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;

public abstract class BaseUpgradeScreen extends Screen {
    protected static final int Y_PADDING = 30;
    protected static final int X_SIZE = 100;
    protected static final int Y_SIZE = 20;
    protected static final int MIN_X_PADDING = 5;
    private static final ResourceLocation DEPENDENCY_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath("crystal_tools", "textures/gui/dependency_line.png");
    private static final float DEPENDENCY_LINE_WIDTH = 9;
    private static final int DEPENDENCY_LINE_IMAGE_WIDTH = 252;
    private static final int DEPENDENCY_LINE_IMAGE_HEIGHT = 256;

    private static int ANIMATION_FRAME = 0;
    private static final int ANIMATION_FRAME_MIN = 0;
    private static int ANIMATION_COUNTER = 0;
    private static final int ANIMATION_COUNTER_MAX = 10;

    protected final Player player;
    protected SkillData data;
    private final HashMap<Integer, SkillButton> skillButtons = new HashMap<>();

    private int xOffset = 0;
    private int yOffset = 0;

    private XpButton xpButton;

    public BaseUpgradeScreen(Player player, Component title) {
        super(title);
        this.player = player;
    }

    protected void init() {
        List<List<SkillDataNode>> tiers = data.getAllNodesByTier();

        int y = Y_PADDING;

        // add skill tree items
        for (List<SkillDataNode> tier : tiers) {
            this.addButtonsFromTier(tier, y);
            y += (Y_PADDING + Y_SIZE);
        }

        this.initComponents();

        this.updateButtons();
    }

    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    protected void initComponents() {
        if (CrystalToolsConfig.EXPERIENCE_PER_SKILL_LEVEL.get() > 0) {
            xpButton = addRenderableWidget(new XpButton(5, getXpButtonY(), 30, Y_SIZE, pButton -> {
                int xpCost = XpUtils.getXPForLevel(getXpLevelCost());
                if (XpUtils.getPlayerTotalXp(player) >= xpCost) {
                    player.giveExperiencePoints(-xpCost);
                    PacketDistributor.sendToServer(new RemoveXpPayload(xpCost));
                    changeSkillPoints(1);
                    updateButtons();
                }
            }, (pButton, guiGraphics, mouseX, mouseY) -> {
                Component textComponent = Component.literal("Use Experience To Level Up");
                guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(BaseUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
            }, getXpLevelCost()));
        }
    }

    protected abstract int getXpButtonY();

    protected abstract void changeSkillPoints(int change);

    private int getXpLevelCost() {
        int xpLevelCost = CrystalToolsConfig.EXPERIENCE_PER_SKILL_LEVEL.get();
        int levelScaling = CrystalToolsConfig.EXPERIENCE_LEVELING_SCALING.get();
        if (levelScaling > 0) {
            xpLevelCost += data.getTotalPoints() / levelScaling;
        }

        return xpLevelCost;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBlockBackground(guiGraphics, CrystalToolsConfig.UPGRADE_SCREEN_BACKGROUND.get());
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        drawDependencyLines(guiGraphics);
        guiGraphics.drawString(font, "Skill Points: " + this.getSkillPoints(), 5, 5, Colors.TEXT_LIGHT);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        ANIMATION_COUNTER++;

        if (ANIMATION_COUNTER > ANIMATION_COUNTER_MAX) {
            ANIMATION_COUNTER = 0;
            if (ANIMATION_FRAME < ANIMATION_FRAME_MIN) ANIMATION_FRAME = 11;
            ANIMATION_FRAME--;
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderTransparentBackground(guiGraphics);
    }

    protected abstract int getSkillPoints();

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addButtonsFromTier(List<SkillDataNode> nodes, int y) {
        int size = nodes.size();
        int paddingX = Math.max((this.width - size * X_SIZE) / (size + 1), MIN_X_PADDING);

        int x = paddingX;

        for (SkillDataNode node : nodes) {
            this.addButtonFromNode(node, x, y);
            x += (paddingX + X_SIZE);
        }
    }

    private void addButtonFromNode(SkillDataNode node, int x, int y) {
        this.addSkillButton(new SkillButton(x, y, X_SIZE, Y_SIZE, Component.literal(node.getName()), (button) -> {
            this.onSkillButtonPress(node, button);
        }, (button, guiGraphics, mouseX, mouseY) -> {
            String text;
            if (node.getType().equals(SkillNodeType.INFINITE) && node.getPoints() > 0) {
                text = node.getDescription() + "\n" + node.getPoints() + " Points";
            } else {
                text = node.getDescription();
            }

            Component textComponent = Component.literal(text);
            guiGraphics.renderTooltip(this.font, this.font.split(textComponent, Math.max(BaseUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, this.data, node, this.player));
    }

    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        List<SkillDataRequirement> requirements = node.getRequirements();

        for (SkillDataRequirement requirement : requirements) {
            if (CrystalToolsConfig.ENABLE_ITEM_REQUIREMENTS.get() && requirement.getRequirementType() == RequirementType.ITEM) {
                SkillItemRequirement itemRequirement = (SkillItemRequirement) (requirement);
                itemRequirement.getItems().forEach(item -> {
                    PacketDistributor.sendToServer(new RemoveItemPayload(item.getDefaultInstance()));
                    InventoryUtils.removeItemFromInventory(this.player.getInventory(), item.getDefaultInstance());
                });
            }
        }

        this.data.addPoint();

        this.updateButtons();
    }

    private void addSkillButton(SkillButton button) {
        this.skillButtons.put(button.getDataNode().getId(), button);
        this.addRenderableWidget(button);
    }

    void updateButtons() {
        int skillPoints = this.getSkillPoints();
        for (SkillButton button : this.skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            button.active = !button.isComplete && node.canLevel(data, this.player) && skillPoints > 0;
            if (node.isComplete()) {
                button.setComplete();
            }
        }

        if (xpButton != null) {
            xpButton.update(getXpLevelCost(), player);
        }
    }

    private void drawDependencyLines(GuiGraphics guiGraphics) {
        for (SkillButton button : skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            for (SkillDataRequirement requirement : node.getRequirements()) {
                if (requirement instanceof SkillDataNodeRequirement nodeRequirement) {
                    List<Integer> nodes = nodeRequirement.getRequiredNodes();

                    for (int j : nodes) {
                        if (this.skillButtons.containsKey(j) && this.skillButtons.get(j).isHovered() || button.isHovered()) {
                            drawDependencyLine(guiGraphics, getButtonCenter(this.skillButtons.get(j)), getButtonCenter(button), this.skillButtons.get(j).getDataNode().getPoints() > 0);
                        }
                    }
                }
            }
        }
    }

    // Scrolling
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.xOffset += (int) dragX;
        this.yOffset += (int) dragY;

        for (SkillButton skillButton : this.skillButtons.values()) {
            skillButton.xOffset = this.xOffset;
            skillButton.yOffset = this.yOffset;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (SkillButton skillButton : this.skillButtons.values()) {
            skillButton.yOffset += (int) scrollY * 10;
        }

        return true;
    }

    private void drawDependencyLine(GuiGraphics guiGraphics, int[] p1, int[] p2, boolean active) {
        drawDependencyLine(guiGraphics, p1[0], p1[1], p2[0], p2[1], active);
    }

    private void drawDependencyLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, boolean active) {
        RenderSystem.setShaderTexture(0, DEPENDENCY_LINE_LOCATION);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);


        float yImageStart = active ? DEPENDENCY_LINE_WIDTH : 0;
        float yImageEnd = active ? DEPENDENCY_LINE_WIDTH * 2 : DEPENDENCY_LINE_WIDTH;
        float length = (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        float angle = (float) Math.atan2((x2 - x1), (y2 - y1));
        float xOffset = DEPENDENCY_LINE_WIDTH / 3 * (float) Math.cos(angle);
        float yOffset = DEPENDENCY_LINE_WIDTH / 3 * (float) Math.sin(angle);

        float x1F = x1 - xOffset;
        float y1F = y1 + yOffset;
        float x2F = x1 + xOffset;
        float y2F = y1 - yOffset;
        float x3F = x2 + xOffset;
        float y3F = y2 - yOffset;
        float x4F = x2 - xOffset;
        float y4F = y2 + yOffset;

//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x2F, y2F, 0).setUv((float) ANIMATION_FRAME / DEPENDENCY_LINE_IMAGE_WIDTH, yImageStart / DEPENDENCY_LINE_IMAGE_HEIGHT);
        bufferbuilder.addVertex(matrix4f, x1F, y1F, 0).setUv((float) ANIMATION_FRAME / DEPENDENCY_LINE_IMAGE_WIDTH, yImageEnd / DEPENDENCY_LINE_IMAGE_HEIGHT);
        bufferbuilder.addVertex(matrix4f, x4F, y4F, 0).setUv((length + ANIMATION_FRAME) / DEPENDENCY_LINE_IMAGE_WIDTH, yImageEnd / DEPENDENCY_LINE_IMAGE_HEIGHT);
        bufferbuilder.addVertex(matrix4f, x3F, y3F, 0).setUv((length + ANIMATION_FRAME) / DEPENDENCY_LINE_IMAGE_WIDTH, yImageStart / DEPENDENCY_LINE_IMAGE_HEIGHT);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    private int[] getButtonCenter(SkillButton button) {
        int x = button.getX() + button.xOffset + button.getWidth() / 2;
        int y = button.getY() + button.yOffset + button.getHeight() / 2;

        return new int[] {x, y};
    }

    public void renderBlockBackground(GuiGraphics guiGraphics, String block) {
        // TODO: Allow modded blocks
        ResourceLocation blockResource = ResourceLocation.withDefaultNamespace("textures/block/" + block + ".png");
        renderMenuBackgroundTexture(guiGraphics, blockResource, 0, 0, 0, 0, width, height);
    }
}
