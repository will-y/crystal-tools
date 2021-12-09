package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.ToolAttributePacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class UpgradeScreen extends Screen {
    private final ItemStack tool;

    // TODO: pass in resource location for the skill tree. For now just make a factory that makes the object
    public UpgradeScreen(ItemStack itemStack) {
        super(new TextComponent("Test Title"));
        tool = itemStack;
    }

    // FOR SOME REASON NEED TO ADD COMPONENTS HERE????
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TextComponent("Add Speed"), (button) -> {
            PacketHandler.sendToServer(new ToolAttributePacket("speed_bonus", 4));
        }, (button, poseStack, mouseX, mouseY) -> {
            Component text = new TextComponent("Text Tooltip");

            UpgradeScreen.this.renderTooltip(poseStack, UpgradeScreen.this.minecraft.font.split(text, Math.max(UpgradeScreen.this.width / 2 - 43, 170)), mouseX, mouseY);

        }));


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


}
