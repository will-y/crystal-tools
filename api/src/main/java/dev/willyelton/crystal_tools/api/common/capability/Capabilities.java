package dev.willyelton.crystal_tools.api.common.capability;

import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.capabilities.ItemCapability;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

public class Capabilities {
    public static final ItemCapability<LevelableStack, RegistryAccess> ITEM_SKILL =
            ItemCapability.create(baseRl("item_skill"),
                    LevelableStack.class,
                    RegistryAccess.class);
}
