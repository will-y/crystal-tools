package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SkillDataNode {
    private final int id;
    private final String name;
    private final String description;
    // If 0 = infinite, 1 = normal, 2+ = amount of points you can put in
    private final int limit;
    private final List<SkillDataRequirement> requirements;
    private final List<Identifier> keys;
    private SkillSubText skillSubText;

    public SkillDataNode(int id, String name, String description, int limit, Identifier key,
                         List<SkillDataRequirement> requirements, SkillSubText skillSubText) {
        this(id, name, description, limit, key == null ? List.of() : List.of(key), requirements, skillSubText);
    }

    public SkillDataNode(int id, String name, String description, int limit, List<Identifier> keys,
                         List<SkillDataRequirement> requirements, SkillSubText skillSubText) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.limit = limit;
        this.keys = keys;
        this.requirements = requirements;
        this.skillSubText = skillSubText;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLimit() {
        return limit;
    }

    public List<SkillDataRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(SkillDataRequirement requirement) {
        requirements.add(requirement);
    }

    public List<Identifier> getKeys() {
        return this.keys;
    }

    public SkillSubText getSkillSubText() {
        return this.skillSubText;
    }

    public void setSubtext(SkillSubText subtext) {
        this.skillSubText = subtext;
    }

    /**
     * Returns true if all of this node's requirements are met, and it can be leveled
     */
    public boolean canLevel(SkillPoints points, Player player) {
        for (SkillDataRequirement requirement : this.requirements) {
            if (!requirement.canLevel(points, player)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "SkillDataNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", limit=" + limit +
                ", requirements=" + requirements +
                ", key='" + (keys != null ? keys.stream().map(Identifier::toString).collect(Collectors.joining(", ")) : "") + '\'' +
                '}';
    }

    public abstract SkillNodeType getSkillNodeType();

    public static Codec<SkillDataNode> CODEC = Identifier.CODEC.xmap(SkillNodeType::fromIdentifier, SkillNodeType::Identifier)
            .dispatch(SkillDataNode::getSkillNodeType, SkillNodeType::codec);

    public static StreamCodec<RegistryFriendlyByteBuf, SkillDataNode> STREAM_CODEC = CodecUtils.RESOURCE_LOCATION_STREAM_CODEC
            .map(SkillNodeType::fromIdentifier, SkillNodeType::Identifier)
            .dispatch(SkillDataNode::getSkillNodeType, SkillNodeType::streamCodec);
}
