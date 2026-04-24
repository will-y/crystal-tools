package dev.willyelton.crystal_tools.api.client.gui;

import dev.willyelton.crystal_tools.api.Registration;
import dev.willyelton.crystal_tools.api.common.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.api.common.inventory.container.LevelableContainerMenu;
import dev.willyelton.crystal_tools.api.common.network.payload.BlockSkillPayload;
import dev.willyelton.crystal_tools.api.common.network.payload.ResetSkillsBlockPayload;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import dev.willyelton.crystal_tools.api.common.skill.SkillPoints;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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
    protected void addPointsForNode(int pointsToSpend, int nodeId) {
        this.container.addToPoints(nodeId, pointsToSpend);
        ClientPacketDistributor.sendToServer(new BlockSkillPayload(nodeId, key, pointsToSpend, container.getBlockEntity().getBlockPos()));
    }

    @Override
    protected void changeClientSkillPoints(int change) {
        this.container.addSkillPoints(change);
    }

    @Override
    protected void resetPoints(boolean crystalRequired) {
        if (!crystalRequired || this.player.getInventory().hasAnyOf(Set.of(Registration.CRYSTAL.get()))) {
            LevelableBlockEntity blockEntity = this.container.getBlockEntity();
            ClientPacketDistributor.sendToServer(new ResetSkillsBlockPayload(blockEntity.getBlockPos()));
            blockEntity.resetSkills();
        }

        this.onClose();
    }

    @Override
    public SkillPoints getPoints() {
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
