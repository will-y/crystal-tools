package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * I hope that this will be the common stuff related to block entities and items
 * There will be different concrete implementations.
 */
public interface Levelable {

    void addExp(Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, float amount);

    default void addExp(Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
        addExp(level, blockPos, livingEntity, 1);
    }

    int getSkillPoints();

    int getExperienceCap();

    SkillData getSkillTree();

    SkillPoints getPointData();

    ResourceKey<SkillData> getKey();

    void increaseExpCap(int levelIncrease);

    default void increaseExpCap() {
        increaseExpCap(1);
    }

    default boolean allowReset() {
        return true;
    }

    default boolean allowXpLevels() {
        return true;
    }
}
