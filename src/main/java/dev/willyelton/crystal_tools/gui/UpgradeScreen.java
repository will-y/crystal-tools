package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import dev.willyelton.crystal_tools.tool.skills.SkillData;
import dev.willyelton.crystal_tools.tool.skills.SkillDataNode;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.system.CallbackI;

import java.util.List;

public class UpgradeScreen extends Screen {
    private final ItemStack tool;
    private final SkillData toolData;

    // TODO: pass in resource location for the skill tree. For now just make a factory that makes the object
    public UpgradeScreen(ItemStack itemStack) {
        super(new TextComponent("Test Title"));
        tool = itemStack;

        // TODO get the type from the item and the int[] from nbt of item
       toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", "skill_trees/pickaxe.json"), new int[] {});

    }

    // FOR SOME REASON NEED TO ADD COMPONENTS HERE????
    protected void init() {
        // Test button
//        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TextComponent("Add Speed"), (button) -> {
//            PacketHandler.sendToServer(new ToolAttributePacket("speed_bonus", 4));
//        }, (button, poseStack, mouseX, mouseY) -> {
//            Component text = new TextComponent("Text Tooltip");
//            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
//        }));

        List<List<SkillDataNode>> tiers = toolData.getAllNodesByTier();

        int x = 20;
        int y = 20;

        for (List<SkillDataNode> tier : tiers) {
            for (SkillDataNode node : tier) {
                this.addButtonFromNode(node, x, y);
                x += 120;
            }
            y += 30;
            x = 20;
        }
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

    private void addButtonFromNode(SkillDataNode node, int x, int y) {
        this.addRenderableWidget(new Button(x, y, 100, 20, new TextComponent(node.getName()), (button) -> {
            // TODO: make this actually the thing it need to be based off of the node
            PacketHandler.sendToServer(new ToolAttributePacket("speed_bonus", 1));
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent(node.getDescription());
            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);
        }));
    }


}
