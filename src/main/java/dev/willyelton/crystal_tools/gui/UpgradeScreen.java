package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class UpgradeScreen extends Screen {
    public UpgradeScreen() {
        super(new TextComponent(""));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        super.render(poseStack, mouseX, mouseY, particleTicks);
        fill(poseStack, 0, 0, 100, 100, 16777216);
    }
}
