package dev.willyelton.crystal_tools.tool.skills;

import java.util.ArrayList;
import java.util.List;

public class SkillDataNode {
    private final int id;
    private final String name;
    private final String description;
    // Maybe make this and enum
    private final String type;
    private int points;
    private final List<SkillDataRequirement> requirements;
    private final String key;
    private final int value;

    public SkillDataNode(int id, String name, String description, String type, int points, String key, int value, List<SkillDataRequirement> requirements) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.points = points;
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

    public String getType() {
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

    public int getValue() {
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
    public boolean canLevel(SkillData data) {
        for (SkillDataRequirement requirement : this.requirements) {
            if (!requirement.canLevel(data)) {
                return false;
            }
        }

        return true;
    }

    public boolean isComplete() {
        return this.points == 1 && this.type.equals("normal");
    }
}
