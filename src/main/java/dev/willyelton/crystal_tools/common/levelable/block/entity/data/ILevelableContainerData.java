package dev.willyelton.crystal_tools.common.levelable.block.entity.data;

import net.minecraft.world.inventory.ContainerData;

public interface ILevelableContainerData extends ContainerData {
    int getSkillPoints();

    void addSkillPoints(int points);

    void addToPoints(int nodeId, int value);

    int getExp();

    int getExpCap();

    int[] getPoints();
}
