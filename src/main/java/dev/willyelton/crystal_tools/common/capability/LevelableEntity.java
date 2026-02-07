package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.SkillTreeData;
import dev.willyelton.crystal_tools.common.levelable.condition.LevelableEntityCondition;
import dev.willyelton.crystal_tools.common.levelable.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// TODO: Can probably redesign this Levelable interface to have less duplication here
public class LevelableEntity implements Levelable {

    public static @Nullable LevelableEntity of(LivingEntity livingEntity, @Nullable RegistryAccess registryAccess) {
        SkillTreeData skillTreeData = livingEntity.getType().builtInRegistryHolder().getData(DataMaps.ENTITY_SKILL_TREES);

        if (skillTreeData == null) {
            return null;
        }

        if (registryAccess == null) {
            return new LevelableEntity(livingEntity, null, null, skillTreeData, getLevelableEntityConditions(skillTreeData));
        }

        Optional<Holder.Reference<SkillData>> skillData = ToolUtils.getEntitySkillData(registryAccess, skillTreeData.treeLocation());

        return skillData.map(skillDataReference -> new LevelableEntity(livingEntity, skillDataReference.value(),
                skillDataReference.key(), skillTreeData, getLevelableEntityConditions(skillTreeData))).orElse(null);
    }

    private static List<LevelableEntityCondition> getLevelableEntityConditions(SkillTreeData skillTreeData) {
        return skillTreeData.conditions().stream()
                .filter(c -> c instanceof LevelableEntityCondition)
                .map(LevelableEntityCondition.class::cast)
                .toList();
    }

    private final LivingEntity entity;
    @Nullable
    private final SkillData skillData;
    @Nullable
    private final ResourceKey<SkillData> key;
    private final SkillTreeData skillTreeData;
    private final List<LevelableEntityCondition> conditions;

    private LevelableEntity(LivingEntity entity, SkillData skillData, ResourceKey<SkillData> key, SkillTreeData skillTreeData, List<LevelableEntityCondition> conditions) {
        this.entity = entity;
        this.skillData = skillData;
        this.key = key;
        this.skillTreeData = skillTreeData;
        this.conditions = conditions;
    }

    @Override
    public void addExp(Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, float amount) {
        EntitySkillData entitySkillData = getEntitySkillData();
        int newExperience = entitySkillData.skillExperience() + (int) (amount * skillTreeData.experienceScaling());
        entitySkillData.setSkillExperience(newExperience);
        int experienceCap = getExperienceCap();

        if (newExperience >= experienceCap) {
            entitySkillData.addSkillPoints(1);

            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            entitySkillData.setSkillExperience(Math.max(0, newExperience - experienceCap));
            increaseExpCap();
        }

        sync();
    }

    @Override
    public int getSkillPoints() {
        return getEntitySkillData().unspentPoints();
    }

    @Override
    public int getExperienceCap() {
        return getEntitySkillData().experienceCap();
    }

    public int getExperience() {
        return getEntitySkillData().skillExperience();
    }

    @Override
    public SkillData getSkillTree() {
        return skillData;
    }

    @Override
    public SkillPoints getPointData() {
        return getEntitySkillData().skillPoints();
    }

    @Override
    public ResourceKey<SkillData> getKey() {
        return key;
    }

    @Override
    public void increaseExpCap(int levelIncrease) {
        int experienceCap = getExperienceCap();
        int newCap = ToolUtils.getNewCap(experienceCap, levelIncrease);
        getEntitySkillData().setExperienceCap(newCap);

        sync();
    }

    public boolean checkConditions(LivingEntity entity, Player player) {
        for (LevelableEntityCondition condition : conditions) {
            if (!condition.check(entity, player)) {
                return false;
            }
        }

        return true;
    }

    private EntitySkillData getEntitySkillData() {
        return entity.getData(ModRegistration.ENTITY_SKILL);
    }

    private void sync() {
        entity.syncData(ModRegistration.ENTITY_SKILL);
    }

    public boolean allowDamageXp() {
        return skillTreeData.allowDamageXp();
    }
}
