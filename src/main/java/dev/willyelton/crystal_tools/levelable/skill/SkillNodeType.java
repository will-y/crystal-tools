package dev.willyelton.crystal_tools.levelable.skill;

public enum SkillNodeType {
    NORMAL,
    INFINITE;

    public static SkillNodeType fromString(String type) {
        if (type.equalsIgnoreCase("normal")) {
            return NORMAL;
        } else if (type.equalsIgnoreCase("infinite")) {
            return INFINITE;
        }

        return NORMAL;
    }
}
