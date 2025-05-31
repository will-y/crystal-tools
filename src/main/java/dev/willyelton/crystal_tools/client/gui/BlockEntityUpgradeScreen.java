package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.gui.component.SkillButton;
import dev.willyelton.crystal_tools.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.network.data.BlockAttributePayload;
import dev.willyelton.crystal_tools.common.network.data.BlockSkillPayload;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsBlockPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BlockEntityUpgradeScreen extends BaseUpgradeScreen {
    private final LevelableContainerMenu container;
    @Nullable
    private final Screen screen;
    @Nullable
    private final Runnable onClose;

    public BlockEntityUpgradeScreen(LevelableContainerMenu container, Player player, Runnable onClose, SkillData data, ResourceKey<SkillData> key) {
        super(player, Component.literal("Upgrade Furnace"), data, key);
        this.container = container;
        this.screen = null;
        this.onClose = onClose;
    }

    public BlockEntityUpgradeScreen(LevelableContainerMenu container, Player player, Screen toOpen, SkillData data, ResourceKey<SkillData> key) {
        super(player, Component.literal("Upgrade Furnace"), data, key);
        this.container = container;
        this.screen = toOpen;
        this.onClose = null;
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            if (screen != null) {
                minecraft.popGuiLayer();
                minecraft.setScreen(this.screen);
            }

            if (onClose != null) {
                super.onClose();
                onClose.run();
            }
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
            // TODO
            this.container.addToPoints(node.getId(), (int) 1 * pointsToSpend);

            // TODO
            PacketDistributor.sendToServer(new BlockAttributePayload(null, 1, node.getId(), pointsToSpend));
            // TODO: get the resource key somehow, look at at other upgrade screen
            PacketDistributor.sendToServer(new BlockSkillPayload(node.getId(), key, pointsToSpend, container.getBlockEntity().getBlockPos()));
            points.addPoints(node.getId(), pointsToSpend);
            if (points.getPoints(node.getId()) >= node.getLimit()) {
                ((SkillButton) button).setComplete();
            }
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
        }

        this.onClose();
    }

    @Override
    public SkillPoints getPoints() {
        // TODO: This has to be a change in ILevelableContainerData
        return this.container.getPoints();
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
