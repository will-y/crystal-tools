package dev.willyelton.crystal_tools.common.levelable.condition;

import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelableConditions {

    private static final Map<Identifier, LevelableCondition> CONDITIONS = new ConcurrentHashMap<>();

    private LevelableConditions() {}

    public static LevelableCondition getCondition(Identifier id) {
        return CONDITIONS.get(id);
    }

    public static void bootstrap() {
        WolfCondition wolfCondition = new WolfCondition();
        CONDITIONS.put(wolfCondition.id(), wolfCondition);
    }

}
