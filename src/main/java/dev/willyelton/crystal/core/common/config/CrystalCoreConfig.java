package dev.willyelton.crystal.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

// TODO: Groups
public class CrystalCoreConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    // Repair
    public static ModConfigSpec.IntValue TOOL_REPAIR_COOLDOWN;
    public static ModConfigSpec.BooleanValue REPAIR_IN_HAND;

    public static ModConfigSpec.BooleanValue ENABLE_ITEM_REQUIREMENTS;
    public static ModConfigSpec.BooleanValue REQUIRE_CRYSTAL_FOR_RESET;
    public static ModConfigSpec.IntValue EXPERIENCE_PER_SKILL_LEVEL;

    // Config things, basic tool things
    public static ModConfigSpec.IntValue BASE_EXPERIENCE_CAP;
    public static ModConfigSpec.IntValue MAX_EXP;
    public static ModConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;

    public static ModConfigSpec.IntValue EXPERIENCE_LEVELING_SCALING;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        setupConfig(configBuilder);
        COMMON_CONFIG = configBuilder.build();
    }

    private static void setupConfig(ModConfigSpec.Builder builder) {
        TOOL_REPAIR_COOLDOWN = builder.comment("Determines the cooldown between tool auto repairs")
                .defineInRange("tool_repair_cooldown", 300, 1, 10000);
        REPAIR_IN_HAND = builder.comment("If true, tools will auto repair while you are holding them")
                .define("repair_in_hand", false);

        ENABLE_ITEM_REQUIREMENTS = builder.comment("Enables or disables item requirements for certain upgrades")
                .define("enable_item_requirements", true);
        REQUIRE_CRYSTAL_FOR_RESET = builder.comment("Require a crystal item in your inventory for resetting skill points")
                .define("require_crystal_for_reset", true);
        EXPERIENCE_PER_SKILL_LEVEL = builder.comment("Determines the number of experience levels you need to gain a duration on a tool. Set to 0 to disable")
                .defineInRange("experience_per_skill_level", 10, 0, 100);

        BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Value for Tools and Armor")
                .defineInRange("base_experience_cap", 75, 1, 10000);
        MAX_EXP = builder.comment("Max exp that can be required to get to the next duration")
                .defineInRange("max_exp", 1000, 1, 100000);
        EXPERIENCE_MULTIPLIER = builder.comment("Multiplier for the experience to the next duration")
                .defineInRange("experience_multiplier", 1.1D, 1D, 5);

        EXPERIENCE_LEVELING_SCALING = builder.comment("Number of levels in a tool before the experience duration costs increases. Set to 0 to disable scaling")
                .defineInRange("experience_leveling_scaling", 10, 0, 100);
    }
}
