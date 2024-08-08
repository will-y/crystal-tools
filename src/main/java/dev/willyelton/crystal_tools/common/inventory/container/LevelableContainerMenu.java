package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.ILevelableContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;

public abstract class LevelableContainerMenu extends BaseContainerMenu {
    private final ILevelableContainerData levelableContainerData;

    protected LevelableContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, ContainerData data) {
        super(menuType, containerId, playerInventory, data);
        levelableContainerData = (ILevelableContainerData) data;
    }

    public int getSkillPoints() {
        return levelableContainerData.getSkillPoints();
    }

    public void addSkillPoints(int points) {
        levelableContainerData.addSkillPoints(points);
    }

    public void addToPoints(int nodeId, int value) {
        levelableContainerData.addToPoints(nodeId, value);
    }

    public int getExp() {
        return levelableContainerData.getExp();
    }

    public int getExpCap() {
        return levelableContainerData.getExpCap();
    }

    public int[] getPoints() {
        return levelableContainerData.getPoints();
    }

    public Player getPlayer() {
        return this.player;
    }

    public abstract String getBlockType();

    public abstract LevelableBlockEntity getBlockEntity();
}
