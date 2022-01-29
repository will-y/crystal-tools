package dev.willyelton.crystal_tools.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CrystalToolsConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;

    // Config things
    public static ForgeConfigSpec.IntValue BASE_EXPERIENCE_CAP;
    public static ForgeConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Value for Tools and Armor")
                .defineInRange("base_experience_cap", 50, 1, 10000);

        EXPERIENCE_MULTIPLIER = builder.comment("Multiplier for the EXP to the next level")
                .defineInRange("experience_multiplier", 1.25D, 1D, 100D);
    }
}
