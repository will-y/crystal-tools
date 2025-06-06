package dev.willyelton.crystal_tools.common.levelable.skill.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeOrSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    public static final MapCodec<NodeOrSkillDataRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.listOf().fieldOf("or_node").forGetter(NodeOrSkillDataRequirement::getRequiredNodes)
    ).apply(instance, NodeOrSkillDataRequirement::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NodeOrSkillDataRequirement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), NodeOrSkillDataRequirement::getRequiredNodes,
            NodeOrSkillDataRequirement::new);

    private final List<Integer> nodes;

    public NodeOrSkillDataRequirement(List<Integer> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean canLevel(SkillPoints points, Player player) {
        for (Integer requiredNode : nodes) {
            int pointsInNode = points.getPoints(requiredNode);

            if (pointsInNode > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Integer> getRequiredNodes() {
        return nodes;
    }

    @Override
    public RequirementType getRequirementType() {
        return RequirementType.NODE_OR;
    }

    @Override
    public MapCodec<? extends SkillDataRequirement> codec() {
        return MAP_CODEC;
    }
}
