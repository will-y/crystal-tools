package dev.willyelton.crystal_tools.common.levelable.skill.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

/**
 * Data that gets attached to an entity for all skill related data
 */
// TODO: Can implement only syncing what needs to be changed to not need to sync the whole object
public class EntitySkillData {
    public static final Codec<EntitySkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("unspentPoints").forGetter(EntitySkillData::unspentPoints),
            Codec.INT.fieldOf("skillExperience").forGetter(EntitySkillData::skillExperience),
            Codec.INT.fieldOf("experienceCap").forGetter(EntitySkillData::experienceCap),
            SkillPoints.CODEC.fieldOf("skillPoints").forGetter(EntitySkillData::skillPoints),
            Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("skillData").forGetter(EntitySkillData::skillData)
    ).apply(instance, EntitySkillData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySkillData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntitySkillData::unspentPoints,
            ByteBufCodecs.VAR_INT, EntitySkillData::skillExperience,
            ByteBufCodecs.VAR_INT, EntitySkillData::experienceCap,
            SkillPoints.STREAM_CODEC, EntitySkillData::skillPoints,
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.FLOAT), EntitySkillData::skillData,
            EntitySkillData::new);

    public static EntitySkillData empty() {
        // TODO: get correct cap? Stored on datamap I think. Looks like can get the entity from an overload
        return new EntitySkillData(0, 0, 50, new SkillPoints(), new HashMap<>());
    }

    private int unspentPoints;
    private int skillExperience;
    private int experienceCap;
    private final SkillPoints skillPoints;
    private final Map<Identifier, Float> skillData;

    private EntitySkillData(int unspentPoints, int skillExperience, int experienceCap, SkillPoints skillPoints, Map<Identifier, Float> skillData) {
        this.unspentPoints = unspentPoints;
        this.skillExperience = skillExperience;
        this.experienceCap = experienceCap;
        this.skillPoints = skillPoints;
        this.skillData = new HashMap<>(skillData);
    }

    public int unspentPoints() {
        return unspentPoints;
    }

    public int skillExperience() {
        return skillExperience;
    }

    public int experienceCap() {
        return experienceCap;
    }

    public SkillPoints skillPoints() {
        return skillPoints;
    }

    public void addSkillPoints(int points) {
        this.unspentPoints += points;
    }

    public void setSkillExperience(int experience) {
        this.skillExperience = experience;
    }

    public void setExperienceCap(int cap) {
        this.experienceCap = cap;
    }

    public void addSkill(Identifier rl, float value) {
        this.skillData.compute(rl, (key, oldValue) -> oldValue == null ? value : oldValue + value);
    }

    public boolean hasSkill(Identifier rl) {
        return this.skillData.containsKey(rl);
    }

    public float getSkillValue(Identifier rl) {
        Float toReturn = this.skillData.get(rl);
        if (toReturn == null) {
            return 0.0F;
        }

        return toReturn;
    }

    public int getSkillValueInt(Identifier rl) {
        return Mth.ceil(getSkillValue(rl));
    }

    private Map<Identifier, Float> skillData() {
        return skillData;
    }
}
