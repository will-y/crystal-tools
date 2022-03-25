package dev.willyelton.crystal_tools.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CrystalToolsConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;

    // Config things
    public static ForgeConfigSpec.IntValue BASE_EXPERIENCE_CAP;
    public static ForgeConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue ARMOR_EXPERIENCE_BOOST;
    public static ForgeConfigSpec.DoubleValue BOW_EXPERIENCE_BOOST;
    public static ForgeConfigSpec.DoubleValue SWORD_EXPERIENCE_BOOST;
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

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Value for Tools and Armor")
                .defineInRange("base_experience_cap", 50, 1, 10000);

        EXPERIENCE_MULTIPLIER = builder.comment("Multiplier for the experience to the next level")
                .defineInRange("experience_multiplier", 1.25D, 1D, 100D);

        ARMOR_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Armor gets. (EXP_GAINED = DAMAGE_TAKEN * ARMOR_EXPERIENCE_BOOST)")
                .defineInRange("armor_experience_boost", 2D, 0.1D, 10000D);

        BOW_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Bows get. (EXP_GAINED = DAMAGE_DONE * BOW_EXPERIENCE_BOOST)")
                .defineInRange("bow_experience_boost", 1D, 0.1D, 10000D);

        SWORD_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Swords get. (EXP_GAINED = DAMAGE_DONE * SWORD_EXPERIENCE_BOOST")
                .defineInRange("sword_experience_boost", 1D, 0.1D, 10000D);

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

    }
}
