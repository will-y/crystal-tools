package dev.willyelton.crystal_tools.common.capability;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ItemCapability;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class Capabilities {
    public static final ItemCapability<Levelable, Level> ITEM_SKILL =
            ItemCapability.create(rl("item_skill"),
                    Levelable.class,
                    Level.class);
}
