package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LevelableBlockEntityData(int skillPoints, SkillPoints points, int exp, int expCap) {
    public static final Codec<LevelableBlockEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("skillPoints").forGetter(LevelableBlockEntityData::skillPoints),
            SkillPoints.CODEC.fieldOf("points").forGetter(LevelableBlockEntityData::points),
            Codec.INT.fieldOf("exp").forGetter(LevelableBlockEntityData::exp),
            Codec.INT.fieldOf("expCap").forGetter(LevelableBlockEntityData::expCap)
    ).apply(instance, LevelableBlockEntityData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelableBlockEntityData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelableBlockEntityData::skillPoints,
            SkillPoints.STREAM_CODEC, LevelableBlockEntityData::points,
            ByteBufCodecs.INT, LevelableBlockEntityData::exp,
            ByteBufCodecs.INT, LevelableBlockEntityData::expCap,
            LevelableBlockEntityData::new);

    public LevelableBlockEntityData(int skillPoints, int expCap) {
        this(skillPoints, new SkillPoints(), 0,expCap);
    }
}
