package dev.willyelton.crystal_tools.common.levelable.condition;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelableConditions {

    private static final Map<ResourceLocation, LevelableCondition> CONDITIONS = new ConcurrentHashMap<>();

    private LevelableConditions() {}

    public static LevelableCondition getCondition(ResourceLocation id) {
        return CONDITIONS.get(id);
    }

    public static void bootstrap() {
        WolfCondition wolfCondition = new WolfCondition();
        CONDITIONS.put(wolfCondition.id(), wolfCondition);
    }

}
