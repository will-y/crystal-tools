package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.world.item.ItemStack;

public enum UseMode {
    HOE,
    SHOVEL,
    AXE,
    TORCH;

    public static UseMode fromString(String string) {
        if (string.equalsIgnoreCase("hoe")) {
            return HOE;
        } else if (string.equalsIgnoreCase("shovel")) {
            return SHOVEL;
        } else if (string.equalsIgnoreCase("axe")) {
            return AXE;
        } else if (string.equalsIgnoreCase("torch")) {
            return TORCH;
        } else {
            return HOE;
        }
    }

    public static UseMode nextMode(ItemStack tool, UseMode mode) {
        switch (mode) {
            case HOE -> {
                return SHOVEL;
            }
            case SHOVEL -> {
                return AXE;
            }
            case AXE -> {
                if (NBTUtils.getFloatOrAddKey(tool, "torch") > 0) {
                    return TORCH;
                } else {
                    return HOE;
                }
            }
            case TORCH -> {
                return HOE;
            }
        }

        return HOE;
    }
}
