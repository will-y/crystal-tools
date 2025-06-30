package dev.willyelton.crystal_tools.common.levelable.skill.requirement;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class NotNodeSkillDataRequirement extends NodeSkillDataRequirement {
    public static final MapCodec<NotNodeSkillDataRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.listOf().fieldOf("not_node").forGetter(NotNodeSkillDataRequirement::getRequiredNodes),
            Codec.INT.listOf().optionalFieldOf("unless", List.of()).forGetter(NotNodeSkillDataRequirement::getUnless)
    ).apply(instance, NotNodeSkillDataRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NotNodeSkillDataRequirement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), NotNodeSkillDataRequirement::getRequiredNodes,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), NotNodeSkillDataRequirement::getUnlessNodes,
            NotNodeSkillDataRequirement::new);

    public NotNodeSkillDataRequirement(List<Integer> requiredNodes, List<Integer> unless) {
        super(requiredNodes, true, unless);
    }

    public List<Integer> getUnlessNodes() {
        return unless;
    }

    @Override
    public MapCodec<? extends SkillDataRequirement> codec() {
        return MAP_CODEC;
    }
}
