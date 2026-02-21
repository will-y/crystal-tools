package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.condition.LevelableCondition;
import net.minecraft.resources.Identifier;

import java.util.List;

public record SkillTreeData(Identifier treeLocation, int baseExperienceModifier, float experienceScaling,
                            boolean allowRepair, boolean allowReset, boolean allowXpLevels, boolean allowMiningXp, boolean allowDamageXp,
                            List<LevelableCondition> conditions) {
    public static Codec<SkillTreeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("treeLocation").forGetter(SkillTreeData::treeLocation),
            Codec.INT.optionalFieldOf("baseExperienceModifier", 0).forGetter(SkillTreeData::baseExperienceModifier),
            Codec.FLOAT.optionalFieldOf("experienceScaling", 1.0F).forGetter(SkillTreeData::experienceScaling),
            Codec.BOOL.optionalFieldOf("allowRepair", true).forGetter(SkillTreeData::allowRepair),
            Codec.BOOL.optionalFieldOf("allowReset", true).forGetter(SkillTreeData::allowReset),
            Codec.BOOL.optionalFieldOf("allowXpLevels", true).forGetter(SkillTreeData::allowXpLevels),
            Codec.BOOL.optionalFieldOf("allowMiningXp", true).forGetter(SkillTreeData::allowMiningXp),
            Codec.BOOL.optionalFieldOf("allowDamageXp", true).forGetter(SkillTreeData::allowDamageXp),
            LevelableCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(SkillTreeData::conditions)
    ).apply(instance, SkillTreeData::new));

    public SkillTreeData(Identifier treeLocation) {
        this(treeLocation, 0, 1, true, true, true, true, true, List.of());
    }

    public SkillTreeData(Identifier treeLocation, float experienceScaling) {
        this(treeLocation, experienceScaling, true, true);
    }

    public SkillTreeData(Identifier treeLocation, float experienceScaling, boolean allowMiningXp, boolean allowDamageXp) {
        this(treeLocation, 0, experienceScaling, true, true, true, allowMiningXp, allowDamageXp, List.of());
    }

    public SkillTreeData(Identifier treeLocation, boolean allowMiningXp, boolean allowDamageXp) {
        this(treeLocation, 0, 1, true, true, true, allowMiningXp, allowDamageXp, List.of());
    }

    public SkillTreeData(Identifier treeLocation, int baseExperienceModifier, float experienceScaling,
                         boolean allowRepair, boolean allowReset, boolean allowXpLevels, boolean allowMiningXp, boolean allowDamageXp) {
        this(treeLocation, baseExperienceModifier, experienceScaling, allowRepair, allowReset, allowXpLevels, allowMiningXp, allowDamageXp, List.of());
    }
}
