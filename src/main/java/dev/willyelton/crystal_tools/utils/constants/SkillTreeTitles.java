package dev.willyelton.crystal_tools.utils.constants;

public class SkillTreeTitles {
    public static final String SILK_TOUCH = "Silk Touch";
    public static final String MODE_SWITCH = "Mode Switch";
    public static final String AUTO_PICKUP = "Auto Pickup";
    public static final String AUTO_REPAIR = "Auto Repair";
    public static final String VEIN_MINING = "Vein Mining";
    public static final String AXE_VEIN_MINING = "Tree Chopper";
    public static final String VEIN_MINING_SUBTEXT = "+1 block range per level";
    public static final String AUTO_SMELTING = "Auto Smelting";
    public static final String TORCH = "Torch";
    public static final String TORCH_SUBTEXT = "Uses 10 durability";
    public static final String LEAF_MINER = "Leaf Miner";
    public static final String ALWAYS_EAT = "Always Edible";
    public static final String EFFECT_SUB_TEXT = "Increases duration for each level";

    // TODO: could this be expanded easily?
    public static final String MINING_3x3 = "3x3 Mining";

    public static String miningSpeed(int level) {
        if (level == 0) {
            return "Infinite Speed";
        }
        return String.format("Mining Speed %s", intToRomanNumeral(level));
    }

    public static String durability(int level) {
        return getName("Durability", level);
    }

    public static String unbreaking(int level) {
        return getName("Unbreaking", level);
    }

    public static String reach(int level) {
        return getName("Reach", level);
    }

    public static String fortune(int level) {
        return String.format("Fortune %s", intToRomanNumeral(level));
    }

    public static String nutrition(int level) {
        return getName("Nutrition", level);
    }

    public static String saturation(int level) {
        return getName("Saturation", level);
    }

    public static String eatSpeed(int level) {
        return getName("Eat Speed", level);
    }

    private static String getName(String title, int level) {
        if (level == 0) {
            return "Infinite " + title;
        }

        return String.format(title + " %s", intToRomanNumeral(level));
    }

    private static String intToRomanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> throw new IllegalArgumentException("Roman numeral not encoded for number " + number);
        };
    }
}
