package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillTreeRegistry;
import dev.willyelton.crystal_tools.common.network.data.BlockAttributePayload;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsBlockPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class BlockEntityUpgradeScreen extends BaseUpgradeScreen {
    private final LevelableContainerMenu container;
    private final Screen screen;

    public BlockEntityUpgradeScreen(LevelableContainerMenu container, Player player, Screen toOpen) {
        super(player, Component.literal("Upgrade Furnace"));
        this.container = container;
        this.data = this.getSkillData();
        this.screen = toOpen;
    }

    protected SkillData getSkillData() {
        int[] points = this.container.getPoints();
        String blockType = this.container.getBlockType();
        SkillData data = SkillTreeRegistry.SKILL_TREES.get(blockType);
        data.applyPoints(points);
        return data;
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.popGuiLayer();
            minecraft.setScreen(this.screen);
        }
    }

    @Override
    protected void onSkillButtonPress(SkillDataNode node, Button button) {
        int skillPoints = this.getSkillPoints();
        boolean shift = hasShiftDown();
        boolean control = hasControlDown();

        if (skillPoints > 0) {
            int pointsToSpend = 1;
            if (node.getLimit() == 0) {
                pointsToSpend = getPointsToSpend(skillPoints, shift, control);
            }
            // Idk if this is a problem that this is int because it is just to sync client
            this.container.addToPoints(node.getId(), (int) node.getValue() * pointsToSpend);

            PacketDistributor.sendToServer(new BlockAttributePayload(node.getKey(), node.getValue(), node.getId(), pointsToSpend));
            node.addPoint(pointsToSpend);
            if (node.isComplete()) {
                ((SkillButton) button).setComplete();
            }

            changeSkillPoints(-pointsToSpend);
        }

        super.onSkillButtonPress(node, button);
    }

    @Override
    protected void changeSkillPoints(int change) {
        this.container.addSkillPoints(change);
        PacketDistributor.sendToServer(new BlockAttributePayload("skill_points", change, -1, 1));
    }

    @Override
    protected void resetPoints(boolean crystalRequired) {
        if (!crystalRequired || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()))) {
            LevelableBlockEntity blockEntity = this.container.getBlockEntity();
            PacketDistributor.sendToServer(new ResetSkillsBlockPayload(blockEntity.getBlockPos()));
            blockEntity.resetSkills();

            data = getSkillData();
        }

        this.onClose();
    }

    @Override
    protected int getSkillPoints() {
        return this.container.getSkillPoints();
    }

    @Override
    protected int getXpButtonY() {
        return 15;
    }
}
