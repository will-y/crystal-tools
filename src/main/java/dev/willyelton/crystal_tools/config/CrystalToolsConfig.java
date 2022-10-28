package dev.willyelton.crystal_tools.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CrystalToolsConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;

    // Config things, basic tool things
    public static ForgeConfigSpec.IntValue BASE_EXPERIENCE_CAP;
    public static ForgeConfigSpec.IntValue MAX_EXP;
    public static ForgeConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;

    // Exp Boosts
    public static ForgeConfigSpec.DoubleValue ARMOR_EXPERIENCE_BOOST;
    public static ForgeConfigSpec.DoubleValue BOW_EXPERIENCE_BOOST;
    public static ForgeConfigSpec.DoubleValue SWORD_EXPERIENCE_BOOST;

    // Repair
    public static ForgeConfigSpec.IntValue TOOL_REPAIR_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue ROCKET_REPAIR_MODIFIER;
    public static ForgeConfigSpec.DoubleValue APPLE_REPAIR_MODIFIER;

    public static ForgeConfigSpec.ConfigValue<String> UPGRADE_SCREEN_BACKGROUND;

    // Oregen
    // Stone
    public static ForgeConfigSpec.BooleanValue GENERATE_STONE_ORE;
    public static ForgeConfigSpec.IntValue STONE_VEIN_SIZE;
    public static ForgeConfigSpec.IntValue STONE_PER_CHUNK;
    public static ForgeConfigSpec.IntValue STONE_BOTTOM;
    public static ForgeConfigSpec.IntValue STONE_TOP;

    // Deepslate
    public static ForgeConfigSpec.BooleanValue GENERATE_DEEPSLATE_ORE;
    public static ForgeConfigSpec.IntValue DEEPSLATE_VEIN_SIZE;
    public static ForgeConfigSpec.IntValue DEEPSLATE_PER_CHUNK;
    public static ForgeConfigSpec.IntValue DEEPSLATE_BOTTOM;
    public static ForgeConfigSpec.IntValue DEEPSLATE_TOP;

    // Misc
    public static ForgeConfigSpec.BooleanValue ENABLE_ITEM_REQUIREMENTS;
    public static ForgeConfigSpec.DoubleValue BACKGROUND_OPACITY;
    public static ForgeConfigSpec.BooleanValue PAUSE_SCREEN;
    public static ForgeConfigSpec.BooleanValue REQUIRE_CRYSTAL_FOR_RESET;

    // Disable Tools
    public static ForgeConfigSpec.BooleanValue DISABLE_PICKAXE;
    public static ForgeConfigSpec.BooleanValue DISABLE_SHOVEL;
    public static ForgeConfigSpec.BooleanValue DISABLE_AXE;
    public static ForgeConfigSpec.BooleanValue DISABLE_SWORD;
    public static ForgeConfigSpec.BooleanValue DISABLE_HOE;
    public static ForgeConfigSpec.BooleanValue DISABLE_AIOT;
    public static ForgeConfigSpec.BooleanValue DISABLE_BOW;
    public static ForgeConfigSpec.BooleanValue DISABLE_ROCKET;

    // Disable Armor
    public static ForgeConfigSpec.BooleanValue DISABLE_ELYTRA;
    public static ForgeConfigSpec.BooleanValue DISABLE_HELMET;
    public static ForgeConfigSpec.BooleanValue DISABLE_CHESTPLATE;
    public static ForgeConfigSpec.BooleanValue DISABLE_LEGGINGS;
    public static ForgeConfigSpec.BooleanValue DISABLE_BOOTS;



    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        // EXP
        BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Value for Tools and Armor")
                .defineInRange("base_experience_cap", 50, 1, 10000);

        MAX_EXP = builder.comment("Max exp that can be required to get to the next level")
                .defineInRange("max_exp", 1000, 1, 100000);

        EXPERIENCE_MULTIPLIER = builder.comment("Multiplier for the experience to the next level")
                .defineInRange("experience_multiplier", 1.05D, 1D, 5);

        ARMOR_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Armor gets. (EXP_GAINED = DAMAGE_TAKEN * ARMOR_EXPERIENCE_BOOST)")
                .defineInRange("armor_experience_boost", 2D, 0.1D, 10000D);

        BOW_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Bows get. (EXP_GAINED = DAMAGE_DONE * BOW_EXPERIENCE_BOOST)")
                .defineInRange("bow_experience_boost", 1D, 0.1D, 10000D);

        SWORD_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Swords get. (EXP_GAINED = DAMAGE_DONE * SWORD_EXPERIENCE_BOOST")
                .defineInRange("sword_experience_boost", 1D, 0.1D, 10000D);

        TOOL_REPAIR_COOLDOWN = builder.comment("Determines the cooldown between tool auto repairs")
                .defineInRange("tool_repair_cooldown", 50, 1, 10000);

        ROCKET_REPAIR_MODIFIER = builder.comment("Increases the repair cooldown for the rocket")
                .defineInRange("rocket_repair_modifier", 10D, 1D, 10000D);

        APPLE_REPAIR_MODIFIER = builder.comment("Increases the repair cooldown for the apple")
                .defineInRange("apple_repair_modifier", 10D, 1D, 10000D);

        UPGRADE_SCREEN_BACKGROUND = builder.comment("The block texture to use for the background of the upgrade screen. Must be a vanilla block name.")
                .define("upgrade_screen_background", "cracked_deepslate_tiles");

        // OREGEN
        // Stone
        GENERATE_STONE_ORE = builder.comment("Whether or not to generate crystal ore")
                .define("stone_ore_generate", false);

        STONE_VEIN_SIZE = builder.comment("The vein size for crystal ore.")
                .defineInRange("stone_ore_vein_size", 5, 1, 20);

        STONE_PER_CHUNK = builder.comment("The amount of veins per chunk for crystal ore.")
                .defineInRange("stone_ore_per_chunk", 1, 1, 100);

        STONE_BOTTOM = builder.comment("The lowest height that crystal ore can generate (given as a height from the bottom of the world).")
                .defineInRange("stone_ore_y_min", 64, 64, 256);

        STONE_TOP = builder.comment("The highest height that crystal ore can generate (given as a height from the bottom of the world).")
                .defineInRange("stone_ore_y_max", 84, 64, 256);

        // Deepslate
        GENERATE_DEEPSLATE_ORE = builder.comment("Whether or not to generate deepslate crystal ore")
                .define("deepslate_ore_generate", true);

        DEEPSLATE_VEIN_SIZE = builder.comment("The vein size for deepslate crystal ore.")
                .defineInRange("deepslate_ore_vein_size", 5, 1, 20);

        DEEPSLATE_PER_CHUNK = builder.comment("The amount of veins per chunk for deepslate crystal ore.")
                .defineInRange("deepslate_ore_per_chunk", 1, 1, 100);

        DEEPSLATE_BOTTOM = builder.comment("The lowest height that deepslate crystal ore can generate (given as a height from the bottom of the world).")
                .defineInRange("deepslate_ore_y_min", 0, 0, 64);

        DEEPSLATE_TOP = builder.comment("The highest height that deepslate crystal ore can generate (given as a height from the bottom of the world).")
                .defineInRange("deepslate_ore_y_max", 20, 0, 64);

        // MISC
        ENABLE_ITEM_REQUIREMENTS = builder.comment("Enables or disables item requirements for certain upgrades")
                .define("enable_item_requirements", true);

        BACKGROUND_OPACITY = builder.comment("Controls the opacity of the skill tree background")
                .defineInRange("background_opacity", 1.0, 0, 1.0);

        PAUSE_SCREEN = builder.comment("If it is true then the skill tree screen pauses the game (in single-player), if false then it doesn't")
                .define("pause_screen", true);

        REQUIRE_CRYSTAL_FOR_RESET = builder.comment("Require a crystal item in your inventory for resetting skill points")
                .define("require_crystal_for_reset", true);

        // Disable Tools
        DISABLE_PICKAXE = builder.comment("Disables Crystal Pickaxe").define("disable_pickaxe", false);
        DISABLE_SHOVEL = builder.comment("Disables Crystal Shovel").define("disable_shovel", false);
        DISABLE_AXE = builder.comment("Disables Crystal Axe").define("disable_axe", false);
        DISABLE_SWORD = builder.comment("Disables Crystal Sword").define("disable_sword", false);
        DISABLE_HOE = builder.comment("Disables Crystal Hoe").define("disable_hoe", false);
        DISABLE_AIOT = builder.comment("Disables Crystal AIOT").define("disable_aiot", false);
        DISABLE_BOW = builder.comment("Disables Crystal Bow").define("disable_bow", false);
        DISABLE_ROCKET = builder.comment("Disables Crystal Rocket").define("disable_rocket", false);

        // Disables Armor
        DISABLE_ELYTRA = builder.comment("Disables Crystal Elytra").define("disable_elytra", false);
        DISABLE_HELMET = builder.comment("Disables Crystal Helmet").define("disable_helmet", false);
        DISABLE_CHESTPLATE = builder.comment("Disables Crystal Chestplate").define("disable_chestplate", false);
        DISABLE_LEGGINGS = builder.comment("Disables Crystal Leggings").define("disable_leggings", false);
        DISABLE_BOOTS = builder.comment("Disables Crystal Boots").define("disable_boots", false);
    }
}
