package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UpgradeScreen extends Screen {
    protected UpgradeScreen(Component component) {
        super(component);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        super.render(poseStack, mouseX, mouseY, particleTicks);
        fill(poseStack, 0, 0, mouseX, mouseY, 16777216);
    }
}
