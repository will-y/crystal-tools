package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillDataNodeRequirement;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.RemoveItemPacket;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillNodeType;
import dev.willyelton.crystal_tools.levelable.skill.requirement.RequirementType;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class BaseUpgradeScreen extends Screen {
    final Player player;
    protected SkillData data;
    private final HashMap<Integer, SkillButton> skillButtons = new HashMap<>();

    protected static final int Y_PADDING = 20;
    protected static final int X_SIZE = 100;
    protected static final int Y_SIZE = 20;
    protected static final int MIN_X_PADDING = 5;

    private int xOffset = 0;
    private int yOffset = 0;

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
    protected abstract void initComponents();

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        this.renderBlockBackground(0, CrystalToolsConfig.UPGRADE_SCREEN_BACKGROUND.get());
        drawDependencyLines(poseStack);
        drawString(poseStack, font, "Skill Points: " + this.getSkillPoints(), 5, 5, Colors.TEXT_LIGHT);

        super.render(poseStack, mouseX, mouseY, particleTicks);
    }

    protected abstract int getSkillPoints();

    @Override
    public boolean isPauseScreen() {
        return CrystalToolsConfig.PAUSE_SCREEN.get();
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
        }, (button, poseStack, mouseX, mouseY) -> {
            String text;
            if (node.getType().equals(SkillNodeType.INFINITE) && node.getPoints() > 0) {
                text = node.getDescription() + "\n" + node.getPoints() + " Points";
            } else {
                text = node.getDescription();
            }

            Component textComponent = Component.literal(text);
            BaseUpgradeScreen.this.renderTooltip(poseStack, BaseUpgradeScreen.this.minecraft.font.split(textComponent, Math.max(BaseUpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, this.data, node, this.player));
    }

    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        List<SkillDataRequirement> requirements = node.getRequirements();

        for (SkillDataRequirement requirement : requirements) {
            if (CrystalToolsConfig.ENABLE_ITEM_REQUIREMENTS.get() && requirement.getRequirementType() == RequirementType.ITEM) {
                SkillItemRequirement itemRequirement = (SkillItemRequirement) (requirement);
                itemRequirement.getItems().forEach(item -> {
                    PacketHandler.sendToServer(new RemoveItemPacket(item.getDefaultInstance()));
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
            button.active = !button.isComplete && node.canLevel(data, this.player) && skillPoints > 0;
            if (node.isComplete()) {
                button.setComplete();
            }
        }
    }

    private void drawDependencyLines(PoseStack poseStack) {
        for (SkillButton button : skillButtons.values()) {
            SkillDataNode node = button.getDataNode();
            for (SkillDataRequirement requirement : node.getRequirements()) {
                if (requirement instanceof SkillDataNodeRequirement nodeRequirement) {
                    RequirementType type = requirement.getRequirementType();
                    int[] nodes = nodeRequirement.getRequiredNodes();

                    for (int j : nodes) {
                        int color = Colors.fromRGB(0, 255, 0);
                        switch (type) {
                            case NODE_OR -> {
                                if (requirement.canLevel(data, this.player)) {
                                    color = Colors.fromRGB(0, 255, 100, 100);
                                } else {
                                    color = Colors.fromRGB(255, 0, 100, 100);
                                }
                            }
                            case NODE_AND -> {
                                if (requirement.canLevel(data, this.player)) {
                                    color = Colors.fromRGB(0, 255, 0);
                                } else {
                                    color = Colors.fromRGB(255, 0, 0);
                                }
                            }
                            case NODE_NOT -> {
                                // no line for now
                                continue;
                            }
                        }
                        // so doesn't crash with dependency that doesn't exist
                        if (this.skillButtons.containsKey(j)) {
                            drawLine(poseStack, getButtonBottomCenter(this.skillButtons.get(j)), getButtonTopCenter(button), color);
                        }
                    }
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
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.lineWidth(4);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, (float)minX, (float)minY, 0.0F).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, (float)maxX, (float)maxY, 0.0F).color(red, green, blue, alpha).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
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

    public void renderBlockBackground(int pVOffset, String block) {
        ResourceLocation blockResource = new ResourceLocation("textures/block/" + block + ".png");
        // just vanilla stuff from renderDirtBackground()
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, blockResource);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) CrystalToolsConfig.BACKGROUND_OPACITY.get().doubleValue());
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / 32.0F + (float)pVOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)pVOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / 32.0F, (float)pVOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)pVOffset).color(64, 64, 64, 255).endVertex();
        tesselator.end();
        // Might still need this?
//        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, new PoseStack()));
    }
}
