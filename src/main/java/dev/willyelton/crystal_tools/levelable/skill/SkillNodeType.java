package dev.willyelton.crystal_tools.levelable.skill;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SkillNodeType implements StringRepresentable {
    NORMAL("normal"),
    INFINITE("infinite");

    private final String id;

    private SkillNodeType(String id) {
        this.id = id;
    }

    public static SkillNodeType fromString(String type) {
        if (type.equalsIgnoreCase("normal")) {
            return NORMAL;
        } else if (type.equalsIgnoreCase("infinite")) {
            return INFINITE;
        }

        return NORMAL;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.id;
    }
}
