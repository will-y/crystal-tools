package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for skill trees by type.
 * Invalidated on reload through {@link net.neoforged.neoforge.event.AddServerReloadListenersEvent}
 */
public class SkillTreeCache {
    public static final Map<ResourceLocation, SkillData> CACHE = new ConcurrentHashMap<>();

    private SkillTreeCache() {

    }

    public static SkillData get(ResourceLocation id) {
        return CACHE.get(id);
    }

    public static void put(ResourceLocation id, SkillData data) {
        CACHE.put(id, data);
    }

    public static void invalidate() {
        CACHE.clear();
    }
}
