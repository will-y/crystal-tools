package dev.willyelton.crystal.core.common.block.entity.model;

import dev.willyelton.crystal.core.common.skill.SkillPoints;
import net.minecraft.world.inventory.ContainerData;

public interface ILevelableContainerData extends ContainerData {
    int getSkillPoints();

    void addSkillPoints(int points);

    void addToPoints(int nodeId, int value);

    int getExp();

    int getExpCap();

    SkillPoints getPoints();
}
