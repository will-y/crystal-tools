package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.tool.skills.SkillData;
import dev.willyelton.crystal_tools.tool.skills.SkillDataNode;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UpgradeScreen extends Screen {
    private final ItemStack tool;
    private final SkillData toolData;
    private final List<SkillButton> buttons = new ArrayList<>();

    private static final int Y_PADDING = 20;
    private static final int X_SIZE = 100;
    private static final int Y_SIZE = 20;

    public UpgradeScreen(ItemStack itemStack) {
        super(new TextComponent("Test Title"));
        tool = itemStack;

        // TODO get the type from the item and the int[] from nbt of item
       toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", "skill_trees/pickaxe.json"), new int[] {});

    }

    // FOR SOME REASON NEED TO ADD COMPONENTS HERE????
    protected void init() {
        List<List<SkillDataNode>> tiers = toolData.getAllNodesByTier();

        int y = Y_PADDING;

        for (List<SkillDataNode> tier : tiers) {
            this.addButtonsFromTier(tier, y);
            y += (Y_PADDING + Y_SIZE);
        }

        this.updateButtons();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        this.renderBackground(poseStack);
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
        this.addButton(new SkillButton(x, y, X_SIZE, Y_SIZE, new TextComponent(node.getName()), (button) -> {
            // TODO: also need to update the nbt int[]
            PacketHandler.sendToServer(new ToolAttributePacket(node.getKey(), node.getValue()));
            ((SkillButton) button).setComplete();
            node.addPoint();
            this.updateButtons();
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent(node.getDescription());
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }, this.toolData, node));
    }

    private void addButton(SkillButton button) {
        this.buttons.add(button);
        this.addRenderableWidget(button);
    }

    private void updateButtons() {
        for (SkillButton button : this.buttons) {
            SkillDataNode node = button.getDataNode();
            button.active = node.canLevel(toolData);
        }
    }

}
