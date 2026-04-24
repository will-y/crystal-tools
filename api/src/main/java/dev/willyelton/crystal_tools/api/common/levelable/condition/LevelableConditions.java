package dev.willyelton.crystal_tools.api.common.levelable.condition;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class LevelableConditions {

    private static final Map<Identifier, LevelableCondition> CONDITIONS = new ConcurrentHashMap<>();

    private LevelableConditions() {}

    public static LevelableCondition getCondition(Identifier id) {
        return CONDITIONS.get(id);
    }

    public static void addCondition(Identifier id, LevelableCondition condition) {
        CONDITIONS.put(id, condition);
    }
}
