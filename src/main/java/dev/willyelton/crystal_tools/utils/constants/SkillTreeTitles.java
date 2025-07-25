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
    public static final String NIGHT_VISION = "Night Vision";
    public static final String CREATIVE_FLIGHT = "Creative Flight";
    public static final String AUTO_TARGET = "Auto Target Mobs";
    public static final String TOTEM_SLOT_SUBTEXT = "Craft with a Totem of Undying to add one";
    public static final String SHIELD_KNOCKBACK = "Shield Knockback";
    public static final String CHANNELING = "Channeling";
    public static final String RIPTIDE_TOGGLE = "Riptide Toggle";
    public static final String SORT = "Sort";
    public static final String STORE = "Store in Inventory";
    public static final String COMPRESS = "Compress";
    // TODO: could this be expanded easily?
    public static final String MINING_3x3 = "3x3 Mining";
    public static final String SHEARS = "Shears";
    public static final String PULL_XP = "Attract EXP";
    public static final String PULL_MOBS = "Attract Mobs";
    public static final String INSTANT_PICKUP = "Instant Pickup";

    // Block Entity Titles
    public static final String AUTO_SPLIT = "Auto Split";
    public static final String AUTO_OUTPUT = "Auto Output";
    public static final String SAVE_FUEL = "Save Fuel";
    public static final String REDSTONE_CONTROL = "Redstone Control";
    public static final String CHUNK_LOADING = "Chunk Loading";
    public static final String METAL_GENERATOR = "Metal Generator";
    public static final String FOOD_GENERATOR = "Food Generator";
    public static final String GEM_GENERATOR = "Gem Generator";

    public static String miningSpeed(int level) {
        if (level == 0) {
            return "Infinite Speed";
        }
        return String.format("Mining Speed %s", intToRomanNumeral(level));
    }

    public static String attackDamage(int level) {
        if (level == 0) {
            return "Infinite Damage";
        }
        return String.format("Attack Damage %s", intToRomanNumeral(level));
    }

    public static String attackSpeed(int level) {
        return getName("Attack Speed", level);
    }

    public static String knockbackResistance(int level) {
        return getName("Knockback Res", level);
    }

    public static String knockback(int level) {
        return getName("Knockback", level);
    }

    public static String lifesteal(int level) {
        return getName("Lifesteal", level);
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

    public static String protection(int level) {
        return getName("Prot", level);
    }

    public static String fireProtection(int level) {
        return getName("Fire Prot", level);
    }

    public static String blastProtection(int level) {
        return getName("Blast Prot", level);
    }

    public static String projectileProtection(int level) {
        return getName("Projectile Prot", level);
    }

    public static String baseArmor(int level) {
        return getName("Base Armor", level);
    }

    public static String toughness(int level) {
        return getName("Toughness", level);
    }

    public static String healthBonus(int level) {
        return getName("Health Bonus", level);
    }

    public static String moveSpeed(int level) {
        return getName("Speed Bonus", level);
    }

    public static String arrowDamage(int level) {
        return getName("Arrow Damage", level);
    }

    public static String arrowSpeed(int level) {
        return getName("Arrow Speed", level);
    }

    public static String drawSpeed(int level) {
        return getName("Draw Speed", level);
    }

    public static String flightDuration(int level) {
        return getName("Flight Duration", level);
    }

    public static String doubleItems(int level) {
        return getName("Double Items", level);
    }

    public static String thorns(int level) {
        return getName("Thorns", level);
    }

    public static String shieldCooldown(int level) {
        return getName("Reduce Cooldown", level);
    }

    public static String flamingShield(int level) {
        return getName("Flaming Shield", level);
    }

    public static String totemSlot(int level) {
        return getName("Totem Slot", level);
    }

    public static String beheading(int level) {
        return getName("Beheading", level);
    }

    public static String capturing(int level) {
        return getName("Capturing", level);
    }

    public static String projectileDamage(int level) {
        return getName("Projectile Damage", level);
    }

    public static String projectileSpeed(int level) {
        return getName("Projectile Speed", level);
    }

    public static String riptide(int level) {
        return getName("Riptide", level);
    }

    public static String capacity(int level) {
        return getName("Capacity", level);
    }

    public static String filterSlots(int level) {
        return getName("Filter Slots", level);
    }

    public static String itemSpeed(int level) {
        return getName("Item Speed", level);
    }

    public static String magnetRange(int level) {
        return getName("Magnet Range", level);
    }

    // Block Entity Titles
    public static String furnaceSpeed(int level) {
        return getName("Furnace Speed", level);
    }

    public static String fuelEfficiency(int level) {
        return getName("Fuel Efficiency", level);
    }

    public static String expBoost(int level) {
        return getName("Exp. Boost", level);
    }

    public static String furnaceSlot(int level) {
        return getName("Slot", level);
    }

    public static String furnaceFuelSlot(int level) {
        return getName("Fuel Slot", level);
    }

    public static String trashFilter(int level) {
        return getName("Trash Filter", level);
    }

    public static String feGeneration(int level) {
        return getName("FE Generation", level);
    }

    public static String feCapacity(int level) {
        return getName("FE Capacity", level);
    }

    private static String getName(String title, int level) {
        if (level == 0) {
            return "Infinite " + title;
        }

        return String.format(title + " %s", intToRomanNumeral(level));
    }

    public static String intToRomanNumeral(int number) {
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
