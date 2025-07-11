package dev.willyelton.crystal_tools.common.capability;

import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.capabilities.ItemCapability;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class Capabilities {
    public static final ItemCapability<LevelableStack, RegistryAccess> ITEM_SKILL =
            ItemCapability.create(rl("item_skill"),
                    LevelableStack.class,
                    RegistryAccess.class);
}
