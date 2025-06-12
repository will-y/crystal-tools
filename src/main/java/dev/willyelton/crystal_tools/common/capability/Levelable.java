package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * I hope that this will be the common stuff related to block entities and items
 * There will be different concrete implementations.
 */
public interface Levelable {

    void addExp(Level level, BlockPos blockPos, LivingEntity livingEntity, int amount);

    default void addExp(Level level, BlockPos blockPos, LivingEntity livingEntity) {
        addExp(level, blockPos, livingEntity, 1);
    }

    int getSkillPoints();

    int getExperienceCap();

    default boolean isDisabled() {
        return false;
    }

    SkillData getSkillTree();

    SkillPoints getPointData();

    ResourceKey<SkillData> getKey();
}
