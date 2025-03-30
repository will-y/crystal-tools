package dev.willyelton.crystal_tools.utils.constants;

public class SkillTreeTitles {
    public static final String SILK_TOUCH = "Silk Touch";
    public static final String MODE_SWITCH = "Mode Switch";

    public static String miningSpeed(int level) {
        return String.format("MiningSpeed %s", intToRomanNumeral(level));
    }

    public static String durability(int level) {
        return String.format("Durability %s", intToRomanNumeral(level));
    }

    public static String unbreaking(int level) {
        return String.format("Unbreaking %s", intToRomanNumeral(level));
    }

    public static String reach(int level) {
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
