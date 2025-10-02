package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.config.CrystalToolsClientConfig;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.client.gui.component.XpButton;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeOrSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NotNodeSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataNodeRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.common.network.data.PointsFromXpPayload;
import dev.willyelton.crystal_tools.common.network.data.RemoveItemPayload;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ListUtils;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class BaseUpgradeScreen extends Screen {
    protected static final int Y_PADDING = 30;
    protected static final int X_SIZE = 100;
    protected static final int Y_SIZE = 20;
    protected static final int MIN_X_PADDING = 5;
    private static final ResourceLocation DEPENDENCY_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath("crystal_tools", "textures/gui/dependency_line.png");
    private static final float DEPENDENCY_LINE_WIDTH = 9;
    private static final int DEPENDENCY_LINE_IMAGE_WIDTH = 252;
    private static final int DEPENDENCY_LINE_IMAGE_HEIGHT = 256;
    private static final int MAX_PER_TIER = 6;

    private static int ANIMATION_FRAME = 0;
    private static final int ANIMATION_FRAME_MIN = 0;
    private static int ANIMATION_COUNTER = 0;
    private static final int ANIMATION_COUNTER_MAX = 10;

    protected final Player player;
    protected SkillPoints points;
    protected final SkillData data;
    protected final ResourceKey<SkillData> key;
    private final HashMap<Integer, SkillButton> skillButtons = new HashMap<>();

    private int xOffset = 0;
    private int yOffset = 0;

    @Nullable
    private XpButton xpButton;
    @Nullable
    private Button resetButton;

    public BaseUpgradeScreen(Player player, Component title, SkillData data, ResourceKey<SkillData> key) {
        super(title);
        this.player = player;
        this.data = data;
        this.key = key;
    }

    @Override
    protected void init() {
        // This is to prevent the buttons messing up on screen resize. Probably a better solution.
        if (this.points == null) {
            this.points = getPoints();
        }

        List<List<SkillDataNode>> tiers = data.getAllNodesByTier();

        int y = Y_PADDING;

        // add skill tree items
        for (List<SkillDataNode> tier : tiers) {
            if (tier.size() > MAX_PER_TIER) {
                for (List<SkillDataNode> subTier : ListUtils.partition(tier, 5)) {
                    this.addButtonsFromTier(subTier, y);
                    y += (Y_PADDING + Y_SIZE);
                }
            } else {
                this.addButtonsFromTier(tier, y);
                y += (Y_PADDING + Y_SIZE);
            }

        }

        this.initComponents();
        this.updateButtons();
    }

    /**
     * Used to init things differently from the default item implementation of the upgrade screen
     */
    protected void initComponents() {
        if (CrystalToolsConfig.EXPERIENCE_PER_SKILL_LEVEL.get() > 0 && allowXpLevels()) {
            xpButton = addRenderableWidget(new XpButton(5, getXpButtonY(), 30, Y_SIZE, pButton -> {
                int pointsToGain = getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown());
                int xpCost = XpUtils.getXpCost(pointsToGain, points.getTotalPoints() + getSkillPoints());
                if (XpUtils.getPlayerTotalXp(player) >= xpCost) {
                    player.giveExperiencePoints(-xpCost);
                    changeClientSkillPoints(pointsToGain);
                    ClientPacketDistributor.sendToServer(new PointsFromXpPayload(pointsToGain, this instanceof UpgradeScreen));
                    updateButtons();
                }
            }, (button, guiGraphics, mouseX, mouseY) -> {
                Component textComponent = Component.literal(String.format("Use Experience To Gain Skill Points (+%d Points)", getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown())));
                guiGraphics.setTooltipForNextFrame(this.font, this.font.split(textComponent, Math.max(BaseUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
            }, () -> XpUtils.getLevelForXp(XpUtils.getXpCost(getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown()), points.getTotalPoints() + getSkillPoints()))));
        }

        boolean resetRequiresCrystal = CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get();
        String text = "Reset Skill Points";
        if (resetRequiresCrystal) text += " (Requires 1 Crystal)";
        Tooltip resetTooltip = Tooltip.create(Component.literal(text));

        if (allowReset()) {
            resetButton = addRenderableWidget(Button.builder(Component.literal("Reset"), (button) -> {
                this.resetPoints(resetRequiresCrystal);

                if (resetRequiresCrystal) {
                    ClientPacketDistributor.sendToServer(new RemoveItemPayload(ModRegistration.CRYSTAL.get().getDefaultInstance()));
                    InventoryUtils.removeItemFromInventory(this.player.getInventory(), ModRegistration.CRYSTAL.get().getDefaultInstance());
                }
            }).bounds(width - 40 - 5, 15, 40, Y_SIZE).tooltip(resetTooltip).build());
        }
    }

    protected boolean allowReset() {
        return true;
    }

    protected boolean allowXpLevels() {
        return true;
    }

    protected abstract int getXpButtonY();

    protected abstract void resetPoints(boolean crystalRequired);

    public abstract SkillPoints getPoints();

    protected abstract void changeClientSkillPoints(int change);

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBlockBackground(guiGraphics, CrystalToolsClientConfig.UPGRADE_SCREEN_BACKGROUND.get());

        drawDependencyLines(guiGraphics);
        guiGraphics.nextStratum();
        guiGraphics.drawString(font, "Skill Points: " + this.getSkillPoints(), 5, 5, -1);

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

            if (node.getLimit() == 1) {
                text = node.getDescription();
            } else {
                int pointsToAdd = getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown());

                if (node.getLimit() == 0) {
                    text = String.format("%s\n%d Points", node.getDescription(), points.getPoints(node.getId()));
                } else {
                    text = String.format("%s\n%d/%d Points", node.getDescription(), points.getPoints(node.getId()), node.getLimit());
                }

                if (pointsToAdd > 1) {
                    text = text + String.format("\n(+ %d Points)", pointsToAdd);
                }
            }

            Optional<SkillSubText> subText = Optional.ofNullable(node.getSkillSubText());

            Component textComponent = Component.literal(text);
            FormattedText compositeComponent;

            if (subText.isPresent()) {
                FormattedText subTextComponent = FormattedText.of("\n" + subText.get().text(),
                        Style.EMPTY.withColor(TextColor.parseColor(subText.get().color()).getOrThrow()));
                compositeComponent = FormattedText.composite(textComponent, subTextComponent);
            } else {
                compositeComponent = FormattedText.composite(textComponent);
            }
            guiGraphics.setTooltipForNextFrame(this.font, this.font.split(compositeComponent, Math.max(BaseUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, node, this.player, this.points));
    }

    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        List<SkillDataRequirement> requirements = node.getRequirements();

        for (SkillDataRequirement requirement : requirements) {
            if (CrystalToolsConfig.ENABLE_ITEM_REQUIREMENTS.get() && requirement.getRequirementType() == RequirementType.ITEM) {
                SkillItemRequirement itemRequirement = (SkillItemRequirement) (requirement);
                itemRequirement.getItems().forEach(item -> {
                    ClientPacketDistributor.sendToServer(new RemoveItemPayload(item.getDefaultInstance()));
                    InventoryUtils.removeItemFromInventory(this.player.getInventory(), item.getDefaultInstance());
                });
            }
        }

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
            button.active = !button.isComplete && node.canLevel(points, this.player) && skillPoints > 0;
            if (points.getPoints(node.getId()) >= node.getLimit() && node.getLimit() != 0) {
                button.setComplete();
            }
        }

        if (xpButton != null) {
            xpButton.update(XpUtils.getXpCost(1, points.getTotalPoints() + getSkillPoints()), player);
        }

        if (resetButton != null) {
            this.resetButton.active = !CrystalToolsConfig.REQUIRE_CRYSTAL_FOR_RESET.get() || this.player.getInventory().hasAnyOf(Set.of(ModRegistration.CRYSTAL.get()));
        }
    }

    private void drawDependencyLines(GuiGraphics guiGraphics) {
        for (SkillButton button : skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            for (SkillDataRequirement requirement : node.getRequirements()) {
                if (requirement instanceof SkillDataNodeRequirement nodeRequirement) {
                    int textureY = 0;
                    boolean not = false;
                    switch (nodeRequirement) {
                        case NotNodeSkillDataRequirement n -> {
                            textureY = 27;
                            not = true;
                            if (!n.getUnlessNodes().isEmpty()) {
                                int unlessNode = n.getUnlessNodes().getFirst();
                                if (this.skillButtons.get(unlessNode).isComplete) {
                                    continue;
                                }
                            }
                        }
                        case NodeSkillDataRequirement ignored -> textureY = 9;
                        case NodeOrSkillDataRequirement ignored -> textureY = 18;
                        default -> {}
                    }

                    List<Integer> nodes = nodeRequirement.getRequiredNodes();

                    for (int j : nodes) {
                        if (this.skillButtons.containsKey(j) && this.skillButtons.get(j).isHovered() || button.isHovered()) {
                            boolean active = points.getPoints(this.skillButtons.get(j).getDataNode().getId()) > 0;

                            if (!active && !not) {
                                textureY = 0;
                            }

                            drawDependencyLine(guiGraphics, getButtonCenter(this.skillButtons.get(j)), getButtonCenter(button), textureY);
                        }
                    }
                }
            }
        }
    }

    // Scrolling
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
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

    private void drawDependencyLine(GuiGraphics guiGraphics, int[] p1, int[] p2, int textureY) {
        drawDependencyLine(guiGraphics, p1[0], p1[1], p2[0], p2[1], textureY);
    }

    private void drawDependencyLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int yImageStart) {
        float yImageEnd = yImageStart + DEPENDENCY_LINE_WIDTH;
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

        guiGraphics.submitGuiElementRenderState(new GuiElementRenderState() {
            @Override
            public void buildVertices(VertexConsumer consumer) {
                consumer.addVertexWith2DPose(new Matrix3x2f(guiGraphics.pose()), x2F, y2F).setUv((float) ANIMATION_FRAME / DEPENDENCY_LINE_IMAGE_WIDTH, (float) yImageStart / DEPENDENCY_LINE_IMAGE_HEIGHT).setColor(255, 255, 255, 255)
                        .addVertexWith2DPose(new Matrix3x2f(guiGraphics.pose()), x1F, y1F).setUv((float) ANIMATION_FRAME / DEPENDENCY_LINE_IMAGE_WIDTH, yImageEnd / DEPENDENCY_LINE_IMAGE_HEIGHT).setColor(255, 255, 255, 255)
                        .addVertexWith2DPose(new Matrix3x2f(guiGraphics.pose()), x4F, y4F).setUv((length + ANIMATION_FRAME) / DEPENDENCY_LINE_IMAGE_WIDTH, yImageEnd / DEPENDENCY_LINE_IMAGE_HEIGHT).setColor(255, 255, 255, 255)
                        .addVertexWith2DPose(new Matrix3x2f(guiGraphics.pose()), x3F, y3F).setUv((length + ANIMATION_FRAME) / DEPENDENCY_LINE_IMAGE_WIDTH, (float) yImageStart / DEPENDENCY_LINE_IMAGE_HEIGHT).setColor(255, 255, 255, 255);
            }

            @Override
            public RenderPipeline pipeline() {
                return RenderPipelines.GUI_TEXTURED;
            }

            @Override
            public TextureSetup textureSetup() {
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                return TextureSetup.singleTexture(textureManager.getTexture(DEPENDENCY_LINE_LOCATION).getTextureView());
            }

            @Override
            public @Nullable ScreenRectangle scissorArea() {
                return guiGraphics.peekScissorStack();
            }

            @Override
            public @Nullable ScreenRectangle bounds() {
                return new ScreenRectangle(x1, y1, x2, y2).transformMaxBounds(guiGraphics.pose());
            }
        });
    }

    private int[] getButtonCenter(SkillButton button) {
        int x = button.getX() + button.xOffset + button.getWidth() / 2;
        int y = button.getY() + button.yOffset + button.getHeight() / 2;

        return new int[] {x, y};
    }

    public void renderBlockBackground(GuiGraphics guiGraphics, String block) {
        ResourceLocation blockResource;
        String[] split = block.split(":");
        if (split.length == 1) {
            blockResource = ResourceLocation.withDefaultNamespace("textures/block/" + block + ".png");
        } else {
            blockResource = ResourceLocation.fromNamespaceAndPath(split[0], "textures/block/" + split[1] + ".png");
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, blockResource, 0, 0, 0, 0, width, height, 32, 32, Colors.fromRGB(255, 255, 255, (int) (CrystalToolsClientConfig.BACKGROUND_OPACITY.get() * 255)));
    }

    protected int getPointsToSpend(int points, boolean shiftDown, boolean controlDown) {
        if (controlDown && shiftDown) {
            return Math.min(points, CrystalToolsClientConfig.CONTROL_POINT_SPEND.get() * CrystalToolsClientConfig.SHIFT_POINT_SPEND.get());
        } else if (controlDown) {
            return Math.min(points, CrystalToolsClientConfig.CONTROL_POINT_SPEND.get());
        } else if (shiftDown) {
            return Math.min(points, CrystalToolsClientConfig.SHIFT_POINT_SPEND.get());
        } else {
            return 1;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (xpButton == null) return super.keyPressed(event);

        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            xpButton.update(XpUtils.getXpCost(getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown()), points.getTotalPoints() + getSkillPoints()), player);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (xpButton == null) return super.keyReleased(event);
        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            xpButton.update(XpUtils.getXpCost(getPointsToSpend(Integer.MAX_VALUE, hasShiftDown(), hasControlDown()), points.getTotalPoints() + getSkillPoints()), player);
        }
        return super.keyReleased(event);
    }

    protected boolean hasShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    protected boolean hasControlDown() {
        return Minecraft.getInstance().hasControlDown();
    }

    protected boolean hasAltDown() {
        return Minecraft.getInstance().hasAltDown();
    }
}
