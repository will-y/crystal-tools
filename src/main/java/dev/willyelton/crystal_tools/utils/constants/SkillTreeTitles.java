package dev.willyelton.crystal_tools.utils.constants;

public class SkillTreeTitles {
    public static final String SILK_TOUCH = "Silk Touch";
    public static final String MODE_SWITCH = "Mode Switch";
    public static final String AUTO_PICKUP = "Auto Pickup";
    public static final String AUTO_REPAIR = "Auto Repair";
    public static final String VEIN_MINING = "Vein Mining";
    public static final String VEIN_MINING_SUBTEXT = "#ABABAB";
    public static final String AUTO_SMELTING = "Auto Smelting";
    public static final String TORCH = "Torch";
    public static final String TORCH_SUBTEXT = "Uses 10 durability";

    // TODO: could this be expanded easily?
    public static final String MINING_3x3 = "3x3 Mining";

    public static String miningSpeed(int level) {
        if (level == 0) {
            return "Infinite Speed";
        }
        return String.format("MiningSpeed %s", intToRomanNumeral(level));
    }

    public static String durability(int level) {
        if (level == 0) {
            return "Infinite Durability";
        }
        return String.format("Durability %s", intToRomanNumeral(level));
    }

    public static String unbreaking(int level) {
        if (level == 0) {
            return "Infinite Unbreaking";
        }
        return String.format("Unbreaking %s", intToRomanNumeral(level));
    }

    public static String reach(int level) {
        if (level == 0) {
            return "Infinite Reach";
        }
        return String.format("Reach %s", intToRomanNumeral(level));
    }

    public static String fortune(int level) {
        return String.format("Fortune %s", intToRomanNumeral(level));
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
