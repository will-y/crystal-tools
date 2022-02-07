package dev.willyelton.crystal_tools.item.tool;

public enum UseMode {
    HOE,
    SHOVEL,
    AXE,
    TORCH,
    NONE;

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
            return NONE;
        }
    }
}
