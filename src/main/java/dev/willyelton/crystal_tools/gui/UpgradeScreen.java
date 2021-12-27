package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.network.ToolHealPacket;
import dev.willyelton.crystal_tools.tool.skills.SkillData;
import dev.willyelton.crystal_tools.tool.skills.SkillDataNode;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// TODO: Scrolling?
public class UpgradeScreen extends Screen {
    private final ItemStack tool;
    private final SkillData toolData;
    private final List<SkillButton> skillButtons = new ArrayList<>();

    private static final int Y_PADDING = 20;
    private static final int X_SIZE = 100;
    private static final int Y_SIZE = 20;

    public UpgradeScreen(ItemStack itemStack) {
        super(new TextComponent("Test Title"));
        tool = itemStack;

        int[] points = NBTUtils.getIntArray(tool, "points");

        // TODO get the type from the item and the int[] from nbt of item
        toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", "skill_trees/pickaxe.json"), points);
    }

    // FOR SOME REASON NEED TO ADD COMPONENTS HERE????
    protected void init() {
        List<List<SkillDataNode>> tiers = toolData.getAllNodesByTier();

        int y = Y_PADDING;

        // add skill tree items
        for (List<SkillDataNode> tier : tiers) {
            this.addButtonsFromTier(tier, y);
            y += (Y_PADDING + Y_SIZE);
        }

        // add button to spend skill points to heal tool
        addRenderableWidget(new Button(5, 15, 30, Y_SIZE, new TextComponent("Heal"), (button) -> {
            PacketHandler.sendToServer(new ToolHealPacket());

        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent("Uses a skill point to fully repair this tool");
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }));

        this.updateButtons();


    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        this.renderBackground(poseStack);
        drawString(poseStack, font, "Skill Points: " + (int) NBTUtils.getFloatOrAddKey(tool, "skill_points"), 5, 5, 16777215);

        super.render(poseStack, mouseX, mouseY, particleTicks);
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
            PacketHandler.sendToServer(new ToolAttributePacket(node.getKey(), node.getValue(), node.getId()));
            node.addPoint();
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }
            this.updateButtons();
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent(node.getDescription());
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, this.toolData, node));
    }

    private void addSkillButton(SkillButton button) {
        this.skillButtons.add(button);
        this.addRenderableWidget(button);
    }

    private void updateButtons() {
        for (SkillButton button : this.skillButtons) {
            SkillDataNode node = button.getDataNode();
            button.active = !button.isComplete && node.canLevel(toolData);
            if (node.isComplete()) {
                button.setComplete();
            }
        }
    }

}
