package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.gui.component.SkillButton;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillTreeRegistry;
import dev.willyelton.crystal_tools.network.packet.BlockAttributePacket;
import dev.willyelton.crystal_tools.network.PacketHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class FurnaceUpgradeScreen extends BaseUpgradeScreen {
    private final CrystalFurnaceContainer container;
    private final Screen screen;

    public FurnaceUpgradeScreen(CrystalFurnaceContainer container, Player player, Screen toOpen) {
        super(player, Component.literal("Upgrade Furnace"));
        this.container = container;
        this.data = this.getSkillData();
        this.screen = toOpen;
    }

    @Override
    protected void initComponents() {

    }

    protected SkillData getSkillData() {
        int[] points = this.container.getPoints();
        String blockType = this.container.getBlockType();
        SkillData data = SkillTreeRegistry.SKILL_TREES.get(blockType);
        data.applyPoints(points);
        return data;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.screen);
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = this.getSkillPoints();

        if (skillPoints > 0) {
            this.container.addSkillPoints(-1);
            // Idk if this is a problem that this is int because it is just to sync client
            this.container.addToPoints(node.getId(), (int) node.getValue());

            PacketHandler.sendToServer(new BlockAttributePacket("skill_points", -1, -1));
            PacketHandler.sendToServer(new BlockAttributePacket(node.getKey(), node.getValue(), node.getId()));
            node.addPoint();
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }
        }

        super.onSkillButtonPress(node, button);
    }

    @Override
    protected int getSkillPoints() {
        return this.container.getSkillPoints();
    }
}
