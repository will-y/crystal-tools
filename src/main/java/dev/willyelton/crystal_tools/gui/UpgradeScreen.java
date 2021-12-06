package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class UpgradeScreen extends Screen {
    public UpgradeScreen() {
        super(new TextComponent("Test Title"));


    }

    // FOR SOME REASON NEED TO ADD COMPONENTS HERE FOR SOME REASON????
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TextComponent("Test Button"), (button) -> {
            System.out.println("Pressed Button");
        }));

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float particleTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, particleTicks);
//        int i = (this.width - 252) / 2;
//        int j = (this.height - 140) / 2;

//        fill(poseStack, 0, 0, 100, 100, 16777216);
//        this.blit(poseStack, 100, 100, 0, 0, 252, 140);

    }


}
