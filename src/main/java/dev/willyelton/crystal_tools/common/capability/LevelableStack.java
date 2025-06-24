package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.SkillTreeData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LevelableStack implements Levelable {

    public static @Nullable LevelableStack of(ItemStack stack, RegistryAccess registryAccess) {
        if (stack.getMaxStackSize() != 1) return null;

        if (!ToolUtils.hasSkillTree(stack)) return null;

        SkillTreeData skillTreeData = stack.getItemHolder().getData(DataMaps.SKILL_TREES);

        if (skillTreeData == null) return null;

        Optional<Holder.Reference<SkillData>> skillData = ToolUtils.getSkillData(registryAccess, skillTreeData.treeLocation());

        return skillData.map(skillDataReference -> new LevelableStack(stack, skillDataReference.value(),
                skillDataReference.key(), skillTreeData)).orElse(null);
    }

    private final ItemStack stack;
    private final SkillData skillData;
    private final SkillPoints points;
    private final ResourceKey<SkillData> key;
    private final SkillTreeData skillTreeData;

    private LevelableStack(ItemStack stack, SkillData skillData, ResourceKey<SkillData> key, SkillTreeData skillTreeData) {
        this.stack = stack;
        this.skillData = skillData;
        this.points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        this.key = key;
        this.skillTreeData = skillTreeData;
    }

    @Override
    public void addExp(Level level, BlockPos blockPos, LivingEntity livingEntity, float amount) {
        int newExperience = DataComponents.addToComponent(stack, DataComponents.SKILL_EXPERIENCE, (int) (amount * skillTreeData.experienceScaling()));
        int experienceCap = getExperienceCap();

        if (newExperience >= experienceCap) {
            // level up
            DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, 1);
            // play level up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            if (livingEntity instanceof Player player) {
                player.displayClientMessage(Component.literal("\u00A7b" + stack.getItem().getName(stack).getString() + " Leveled Up (" + getSkillPoints() + " Unspent Points)"), true);
            }
            stack.set(DataComponents.SKILL_EXPERIENCE, Math.max(0, newExperience - experienceCap));
            increaseExpCap();
        }
    }

    @Override
    public int getSkillPoints() {
        return stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    @Override
    public int getExperienceCap() {
        if (stack.has(DataComponents.EXPERIENCE_CAP)) {
            return stack.getOrDefault(DataComponents.EXPERIENCE_CAP, 0);
        } else {
            int toReturn = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get() + skillTreeData.baseExperienceModifier();
            stack.set(DataComponents.EXPERIENCE_CAP, toReturn);

            return toReturn;
        }
    }

    @Override
    public SkillData getSkillTree() {
        return skillData;
    }

    @Override
    public SkillPoints getPointData() {
        return points;
    }

    @Override
    public ResourceKey<SkillData> getKey() {
        return key;
    }

    @Override
    public void increaseExpCap(int levelIncrease) {
        int experienceCap = getExperienceCap();
        int newCap = ToolUtils.getNewCap(experienceCap, levelIncrease);
        stack.set(DataComponents.EXPERIENCE_CAP, newCap);
    }

    @Override
    public boolean allowReset() {
        return skillTreeData.allowReset();
    }

    @Override
    public boolean allowXpLevels() {
        return skillTreeData.allowXpLevels();
    }

    public boolean allowRepair() {
        return skillTreeData.allowRepair();
    }

    public boolean allowMiningXp() {
        return skillTreeData.allowMiningXp();
    }

    public boolean allowDamageXp() {
        return skillTreeData.allowDamageXp();
    }
}
