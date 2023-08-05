package dev.willyelton.crystal_tools.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SkillDataNode {
    private final int id;
    private final String name;
    private final String description;
    private final SkillNodeType type;
    private int points;
    private final List<SkillDataRequirement> requirements;
    private final String key;
    private final float value;

    public SkillDataNode(int id, String name, String description, SkillNodeType type, String key, float value, List<SkillDataRequirement> requirements) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.points = 0;
        this.key = key;
        this.value = value;
        this.requirements = requirements;
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

    public SkillNodeType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public List<SkillDataRequirement> getRequirements() {
        return requirements;
    }

    public int addPoint() {
        return addPoint(1);
    }

    public String getKey() {
        return this.key;
    }

    public float getValue() {
        return this.value;
    }

    public int addPoint(int points) {
        this.points += points;
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Returns true if all of this node's requirements are met, and it can be leveled
     */
    public boolean canLevel(SkillData data, Player player) {
        for (SkillDataRequirement requirement : this.requirements) {
            if (!requirement.canLevel(data, player)) {
                return false;
            }
        }

        return true;
    }

    public boolean isComplete() {
        return this.points >= 1 && this.type.equals(SkillNodeType.NORMAL);
    }

    @Override
    public String toString() {
        return "SkillDataNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", points=" + points +
                ", requirements=" + requirements +
                ", key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public static final Codec<SkillDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(SkillDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(SkillDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(SkillDataNode::getDescription),
            StringRepresentable.fromEnum(SkillNodeType::values).fieldOf("type").forGetter(SkillDataNode::getType),
            Codec.STRING.fieldOf("key").forGetter(SkillDataNode::getKey),
            Codec.FLOAT.fieldOf("value").forGetter(SkillDataNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(SkillDataNode::getRequirements)
    ).apply(instance, SkillDataNode::new));

}
