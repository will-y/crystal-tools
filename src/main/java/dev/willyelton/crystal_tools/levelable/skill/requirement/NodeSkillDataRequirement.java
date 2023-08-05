package dev.willyelton.crystal_tools.levelable.skill.requirement;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    List<Integer> requiredNodes;
    boolean inverse;
    List<Integer> unless;

    public NodeSkillDataRequirement(List<Integer> requiredNodes) {
        this(requiredNodes, false);
    }

    /**
     * Because of codecs, these should only be called by subclasses
     */
    protected NodeSkillDataRequirement(List<Integer> requiredNodes, boolean inverse) {
        this(requiredNodes, inverse, List.of());
    }

    /**
     * Because of codecs, these should only be called by subclasses
     */
    protected NodeSkillDataRequirement(List<Integer> requiredNodes, boolean inverse, List<Integer> unless) {
        this.requiredNodes = requiredNodes;
        this.inverse = inverse;
        this.unless = unless;
    }

    @Override
    public boolean canLevel(SkillData data, Player player) {
        boolean toReturn = true;
        List<SkillDataNode> nodes = data.getAllNodes();
        for (SkillDataNode node : nodes) {
            if (requiredNodes.contains(node.getId())) {
                if ((!inverse && node.getPoints() == 0) || (inverse && node.getPoints() > 0)) {
                    toReturn = false;
                }
            }

            if (inverse && unless.contains(node.getId()) & node.getPoints() > 0) {
                return true;
            }
        }
        return toReturn;
    }

    @Override
    public List<Integer> getRequiredNodes() {
        return requiredNodes;
    }

    protected List<Integer> getUnless() {
        return unless;
    }

    @Override
    public RequirementType getRequirementType() {
        return inverse ? RequirementType.NODE_NOT : RequirementType.NODE_AND;
    }

    @Override
    public JsonElement toJson() {
        return CodecUtils.encodeOrThrow(CODEC, this);
    }

    public static final Codec<NodeSkillDataRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("node").forGetter(NodeSkillDataRequirement::getRequiredNodes)
    ).apply(instance, NodeSkillDataRequirement::new));
}
