package dev.willyelton.crystal_tools.api.utils.constants;

import dev.willyelton.crystal_tools.api.common.block.entity.action.ActionType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class ApiConstants {
    private ApiConstants() {}

    public static final String CORE_MOD_ID = "crystal_core";
    public static final ResourceKey<Registry<ActionType<?, ?>>> ACTION_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(baseRl("action_types"));


    public static final int FILTER_SLOTS_PER_ROW = 9;
    // TODO: Rename
    public static final int TOP_BAR_HEIGHT = 17;
    public static final int ROW_HEIGHT = 18;

    public static Identifier baseRl(String path) {
        return Identifier.fromNamespaceAndPath(CORE_MOD_ID, path);
    }
}
