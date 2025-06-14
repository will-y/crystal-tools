package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SkillTreeData(ResourceLocation treeLocation, int baseExperienceModifier, float experienceScaling,
                            boolean allowRepair, boolean allowReset, boolean allowXpLevels) {
    public static Codec<SkillTreeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("treeLocation").forGetter(SkillTreeData::treeLocation),
            Codec.INT.optionalFieldOf("baseExperienceModifier", 0).forGetter(SkillTreeData::baseExperienceModifier),
            Codec.FLOAT.optionalFieldOf("experienceScaling", 1.0F).forGetter(SkillTreeData::experienceScaling),
            Codec.BOOL.optionalFieldOf("allowRepair", true).forGetter(SkillTreeData::allowRepair),
            Codec.BOOL.optionalFieldOf("allowReset", true).forGetter(SkillTreeData::allowReset),
            Codec.BOOL.optionalFieldOf("allowXpLevels", true).forGetter(SkillTreeData::allowReset)
    ).apply(instance, SkillTreeData::new));

    public SkillTreeData(ResourceLocation treeLocation) {
        this(treeLocation, 0, 1, true, true, true);
    }

    public SkillTreeData(ResourceLocation treeLocation, float experienceScaling) {
        this(treeLocation, 0, experienceScaling, true, true, true);
    }
}
